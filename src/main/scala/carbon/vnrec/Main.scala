package carbon.vnrec

import carbon.vnrec.db.Vndb
import org.apache.spark.{SparkConf, SparkContext}

object Main {
  def main(args: Array[String]): Unit = {
    val sc = {
      val conf = new SparkConf()
        .setMaster("local[8]")
        .setAppName("vnrec")
      val sc = new SparkContext(conf)
      sc.setLogLevel("WARN")
      sc
    }

    val db = new Vndb(sc)
    val engine = new RecommendationEngine(db)

    val initialID = "v23190"
    val initialTitle = db.matchTitle(initialID)

    val recommendations = engine
      .recommend(50, initialID)

    println("Recommendations for " + initialTitle + ":")
    for (rec <- recommendations) {
      println(rec.id + ": " + db.matchTitle(rec.id) + " (" + (rec.strength * 100).round / 100.0 + ")")
    }

    sc.stop()
  }
}
