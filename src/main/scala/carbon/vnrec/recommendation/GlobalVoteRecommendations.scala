package carbon.vnrec.recommendation

import carbon.vnrec.db.Id.IdType
import org.apache.spark.graphx.VertexRDD

trait GlobalVoteRecommendations extends RecommendationBase {
  protected def recommendByVotes(initialID: IdType): VertexRDD[Double] = {
    val shortcuts = db
      .ulist_vns
      .cartesian(db.ulist_vns)
      .filter { case (v1, v2) => v1.uid == v2.uid && v1.vid < v2.vid }
      .map { case (v1, v2) =>
        ((v1.vid, v2.vid),
        ((v1.vote / 100.0) * (v2.vote / 100.0), v1.vote / 100.0, v2.vote / 100.0)) }
      .reduceByKey((a, b) => (a._1 + b._1, a._2 + b._2, a._3 + b._3))
      .cache()

    VertexRDD(
      shortcuts
        .filter(e => e._1._1 == initialID)
        .map(e => (e._1._2, e._2._1 / e._2._2 * math.log(e._2._2)))
        .union(
          shortcuts
            .filter(e => e._1._2 == initialID)
            .map(e => (e._1._1, e._2._1 / e._2._3 * math.log(e._2._3)))
        )
    )
  }
}
