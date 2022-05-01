package carbon.vnrec

import carbon.vnrec.db.Id.IdType
import carbon.vnrec.recommendation.Recommendation

trait VnRecommendationProvider {
  def recommend(n: Int, initialID: IdType): Array[Recommendation]
}
