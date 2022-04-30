package carbon.vnrec.recommendation

import carbon.vnrec.db.Id.IdType
import org.apache.spark.rdd.RDD

trait TagRecommendations extends RecommendationBase {

  protected def recommendByTags(initialID: IdType): RDD[(IdType, Double)] = {
    db.normalizedTagVotes
      .filter(_.vid == initialID)
      .keyBy(_.tag)
      .join(db
        .normalizedTagVotes
        .filter(_.vid != initialID)
        .keyBy(_.tag))
      .values
      .keyBy(_._2.vid)
      .mapValues(x => x._1.vote * x._2.vote)
      .reduceByKey(_ + _)
  }
}
