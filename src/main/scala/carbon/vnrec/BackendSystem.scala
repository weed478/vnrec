package carbon.vnrec
import carbon.vnrec.db.{DirectoryDataProvider, Vndb}
import carbon.vnrec.recommendation.{Recommendation, RecommendationEngine}
import org.apache.spark.{SparkConf, SparkContext}

object BackendSystem extends VnQueryProvider with VnRecommendationProvider {
  private val sc = {
    val conf = new SparkConf()
      .setMaster("local[8]")
      .setAppName("vnrec")
    val sc = SparkContext.getOrCreate(conf)
    sc.setLogLevel("WARN")
    sc
  }
  private val data = new DirectoryDataProvider(sc, "data")
  private val db = new Vndb(data)
  private val engine = new RecommendationEngine(db)

  override def matchTitle(vid: Long): Option[String] =
    Some(db.matchTitle(vid))

  override def search(pattern: String): Array[Long] =
    db.search(pattern).take(10)

  override def recommend(n: Int, initialID: Long): Array[Recommendation] =
    engine.recommend(initialID).take(n)
}
