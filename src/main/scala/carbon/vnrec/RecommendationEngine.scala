package carbon.vnrec

import carbon.vnrec.db.Id.IdType
import carbon.vnrec.db.Vndb
import org.apache.spark.graphx.{Edge, Graph, VertexRDD}
import org.apache.spark.rdd.RDD

class Recommendation(val id: IdType,
                     val strength: Double)
extends Serializable

class RecommendationEngine(private val db: Vndb) {

  def recommend(initialID: IdType): RDD[Recommendation] = {
    normalize(recommendByVotes(initialID))
      .join(normalize(recommendByTags(initialID)))
      .mapValues(w => 0.3 * w._1 + 0.7 * w._2)
      .map(x => new Recommendation(x._1, x._2))
      .sortBy(_.strength, ascending = false)
  }

  private def normalize(data: VertexRDD[Double]): VertexRDD[Double] = {
    val max = data.values.max()
    data.mapValues(_ / max)
  }

  private def aggregateIntoDst(graph: Graph[Double, Double]): VertexRDD[Double] =
    graph.aggregateMessages[Double](
      e => e.sendToDst(e.srcAttr * e.attr),
        _ + _
    )

  private def initialVnWith(other: RDD[IdType], initialId: IdType): VertexRDD[Double] =
    VertexRDD(db.vn
      .map(vn => (vn.id, if (vn.id == initialId) 1.0 else 0.0))
      .union(other.map(k => (k, 0.0)))
    )

  private def recommendByTags(initialID: IdType): VertexRDD[Double] = {
    val initialWeights = initialVnWith(db.tags.map(_.id), initialID)

    val tagVotes = db.normalizedTagVotes
      .map(t => new Edge(t.vid, t.tag, t.vote))

    val tagImportance = aggregateIntoDst(Graph(initialWeights, tagVotes))

    val similarity = aggregateIntoDst(Graph(tagImportance, tagVotes).reverse)

    similarity.filter(_._1 != initialID)
  }

  private def recommendByVotes(initialID: IdType): VertexRDD[Double] = {
    val vertices = initialVnWith(db.users.map(_.id), initialID)

    val edges = db.ulist_vns
      .map(uvn => new Edge(
        uvn.uid,
        uvn.vid,
        uvn.vote / 100.0
      ))

    val credibility = aggregateIntoDst(Graph(vertices, edges).reverse)

    val biasedRatings = Graph(credibility, edges)
      .aggregateMessages[(Double, Double)](
        triple => triple.sendToDst((triple.srcAttr * triple.attr, triple.srcAttr)),
        (a, b) => (a._1 + b._1, a._2 + b._2)
      )
      .mapValues(x => x._1 / x._2 * math.log(x._2))
      .filter(!_._2.isNaN)

    biasedRatings.filter(_._1 != initialID)
  }
}
