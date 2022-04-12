package carbon.vnrec

import carbon.vnrec.recommendation.Recommendation

trait VnRecommendationProvider {
  def recommend(n: Int, initialID: Long): Array[Recommendation]
}
