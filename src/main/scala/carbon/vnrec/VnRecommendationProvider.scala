package carbon.vnrec

trait VnRecommendationProvider {
  def recommend(n: Int, initialID: Long): Array[Recommendation]
}
