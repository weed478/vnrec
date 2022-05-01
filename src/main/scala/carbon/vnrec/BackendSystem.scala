package carbon.vnrec
import carbon.vnrec.db.Id.IdType
import carbon.vnrec.db.{DataProvider, VndbRaw}
import carbon.vnrec.recommendation.{Recommendation, RecommendationEngine}

class BackendSystem (data: DataProvider)
  extends VnQueryProvider with VnRecommendationProvider {

  private val db = new VndbRaw(data)
  private val engine = new RecommendationEngine(db)

  override def matchTitle(vid: IdType): Option[String] =
    Some(db.matchTitle(vid))

  override def search(pattern: String): Array[IdType] =
    db.search(pattern).take(10)

  override def recommend(n: Int, initialID: IdType): Array[Recommendation] =
    engine.recommend(initialID).top(n)(Ordering.by(_.strength))
}
