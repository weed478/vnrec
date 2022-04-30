package carbon.vnrec.recommendation

import carbon.vnrec.db.Id.IdType
import org.apache.spark.rdd.RDD

trait VoteRecommendations extends RecommendationBase {

  protected def recommendByVotes(initialID: IdType): RDD[(Long, Double)] = {
    db.ulist_vns
      .filter(_.vid == initialID)
      .keyBy(_.uid)
      .join(db
        .ulist_vns
        .filter(_.vid != initialID)
        .keyBy(_.uid))
      .values
      .keyBy(_._2.vid)
      .mapValues { case (voteForInitial, voteForOther) =>
        val cred = voteForInitial.vote / 100.0
        val vote = voteForOther.vote / 100.0
        (cred * vote, cred)
      }
      .reduceByKey((a, b) => (a._1 + b._1, a._2 + b._2))
      .mapValues(x => x._1 / x._2 * math.log(x._2))
  }
}
