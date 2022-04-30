package carbon.vnrec.recommendation

import carbon.vnrec.db.Id.IdType
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

trait GlobalTagRecommendations extends RecommendationBase {
  private val shortcuts = db
    .normalizedTagVotes.keyBy(_.tag)
    .join(db.normalizedTagVotes.keyBy(_.tag))
    .map(_._2)
    .filter { case (tv1, tv2) => tv1.vid < tv2.vid }
    .keyBy(e => (e._1.vid, e._2.vid))
    .mapValues { case (tv1, tv2) => tv1.vote * tv2.vote }
    .reduceByKey(_ + _)
    .persist(StorageLevel.DISK_ONLY)

  protected def recommendByTags(initialID: IdType): RDD[(IdType, Double)] = {
    shortcuts
      .filter(e => e._1._1 == initialID || e._1._2 == initialID)
      .map(e =>
        if (e._1._1 == initialID)
          (e._1._2, e._2)
        else
          (e._1._1, e._2)
      )
  }
}
