package carbon.vnrec

import carbon.vnrec.db.Vndb
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD

class Recommendation(val id: Long,
                     val strength: Double)
extends Serializable

class RecommendationEngine(private val db: Vndb) {
  def recommend(initialID: Long): RDD[Recommendation] = {
    normalize(recommendByVotes(initialID))
      .join(normalize(recommendByTags(initialID)))
      .mapValues(w => (w._1 + w._2) / 2.0)
      .map(x => new Recommendation(x._1, x._2))
      .sortBy(_.strength, ascending = false)
  }

  private def normalize(data: RDD[(Long, Double)]): RDD[(Long, Double)] = {
    val max = data.values.max()
    data.mapValues(_ / max)
  }

  private def recommendByTags(initialID: Long): RDD[(Long, Double)] = {
    val initialWeights = db.vn
      .map(vn => (vn.id, if (vn.id == initialID) 1.0 else 0.0))
      .union(db.tags.map(tag => (tag.id, 0.0)))

    val tagVotes = db.tags_vn
      .keyBy(t => (t.vid, t.tag))
      .mapValues(t => (t.vote, 1L))
      .reduceByKey((t1, t2) => (t1._1 + t2._1, t1._2 + t2._2))
      .mapValues(t => t._1 / t._2 * Math.log(t._2))
      .map(t => new Edge(
        t._1._1,
        t._1._2,
        t._2
      ))

    val tagImportance = Graph(initialWeights, tagVotes)
      .aggregateMessages[Double](
        // tag importance += vn weight * tag vote
        e => e.sendToDst(e.srcAttr * e.attr),
        _ + _
      )

    val similarity = Graph(tagImportance, tagVotes)
      .aggregateMessages[Double](
        // vn similarity += tag importance * tag_vote
        e => e.sendToSrc(e.dstAttr * e.attr),
        _ + _
      )

    db.vn.map(_.id)
      .filter(_ != initialID)
      .keyBy(identity)
      .join(similarity)
      .values
  }

  private def recommendByVotes(initialID: Long): RDD[(Long, Double)] = {
    val vertices = db.vn
      .map(vn => (vn.id, if (vn.id == initialID) 1.0 else 0.0))
      .union(db.users
        .map(user => (user.id, 0.0))
      )

    val edges = db.ulist_vns
      .map(uvn => new Edge(
        uvn.uid,
        uvn.vid,
        uvn.vote / 100.0
      ))

    val credibility = Graph(vertices, edges)
      .aggregateMessages[Double](
        triple => triple.sendToSrc(triple.dstAttr * triple.attr),
        _ + _
      )

    val biasedRatings = Graph(credibility, edges)
      .aggregateMessages[(Double, Double)](
        triple => triple.sendToDst((triple.srcAttr * triple.attr, triple.srcAttr)),
        (a, b) => (a._1 + b._1, a._2 + b._2)
      )
      .mapValues(x => x._1 / x._2 * math.log(x._2))
      .filter(!_._2.isNaN)

    db.vn.map(_.id)
      .filter(_ != initialID)
      .keyBy(identity)
      .join(biasedRatings)
      .values
  }
}
