package carbon.vnrec.recommendation

import carbon.vnrec.db.Id.IdType
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

trait GlobalVoteRecommendations extends RecommendationBase {
  private val shortcuts = db
    .ulist_vns.keyBy(_.uid)
    .join(db.ulist_vns.keyBy(_.uid))
    .map(_._2)
    .filter { case (uvn1, uvn2) => uvn1.vid < uvn2.vid }
    .keyBy(e => (e._1.vid, e._2.vid))
    .mapValues { case (uvn1, uvn2) =>
      val v1 = uvn1.vote / 100.0
      val v2 = uvn2.vote / 100.0
      (v1 * v2, v1, v2)
    }
    .reduceByKey((a, b) => (a._1 + b._1, a._2 + b._2, a._3 + b._3))
    .mapValues(x => {
      val v12 = x._1 / x._2 * math.log(x._2)
      val v21 = x._1 / x._3 * math.log(x._3)
      (v12, v21)
    })
    .persist(StorageLevel.DISK_ONLY)

  protected def recommendByVotes(initialID: IdType): RDD[(IdType, Double)] = {
    shortcuts
      .filter(e => e._1._1 == initialID || e._1._2 == initialID)
      .map(e =>
        if (e._1._1 == initialID)
          (e._1._2, e._2._1)
        else
          (e._1._1, e._2._2)
      )
  }
}
