package carbon.vnrec.recommendation

import carbon.vnrec.db.Id.IdType
import carbon.vnrec.db.Vndb
import org.apache.spark.rdd.RDD

class RecommendationEngine(protected val db: Vndb)
  extends GlobalVoteRecommendations with TagRecommendations {

  def recommend(initialID: IdType): RDD[Recommendation] = {
    normalize(recommendByVotes(initialID))
      .join(normalize(recommendByTags(initialID)))
      .mapValues(w => 0.3 * w._1 + 0.7 * w._2)
      .map(x => new Recommendation(x._1, x._2))
      .sortBy(_.strength, ascending = false)
  }
}
