package carbon.vnrec
import carbon.vnrec.db.{DataProvider, VndbRaw}
import carbon.vnrec.recommendation.{Recommendation, RecommendationEngine}

class BackendSystem (data: DataProvider)
  extends VnQueryProvider with VnRecommendationProvider {

  private val db = new VndbRaw(data)
  private val engine = new RecommendationEngine(db)

  override def matchTitle(vid: Long): Option[String] =
    Some(db.matchTitle(vid))

  override def search(pattern: String): Array[Long] =
    db.search(pattern).take(10)

  override def recommend(n: Int, initialID: Long): Array[Recommendation] =
    engine.recommend(initialID).take(n)
}
