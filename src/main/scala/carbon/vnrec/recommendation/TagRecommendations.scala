package carbon.vnrec.recommendation

import carbon.vnrec.db.Id.IdType
import org.apache.spark.graphx.{Edge, Graph, VertexRDD}

trait TagRecommendations extends RecommendationBase {

  protected def recommendByTags(initialID: IdType): VertexRDD[Double] = {
    val initialWeights = initialVnWith(db.tags.map(_.id), initialID)

    val tagVotes = db.normalizedTagVotes
      .map(t => new Edge(t.vid, t.tag, t.vote))

    val tagImportance = aggregateIntoDst(Graph(initialWeights, tagVotes))

    val similarity = aggregateIntoDst(Graph(tagImportance, tagVotes).reverse)

    similarity.filter(_._1 != initialID)
  }
}
