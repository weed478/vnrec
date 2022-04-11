package carbon.vnrec

import carbon.vnrec.db.{Id, Vndb}
import org.apache.spark.{SparkConf, SparkContext}

object Cli {
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

    args match {
      case Array("search", pattern) =>
        db.search(pattern).collect.foreach(vid => {
          println(Id(vid) + ": " + db.matchTitle(vid))
        })

      case Array("recommend", mode, n, initialID) =>
        val initialTitle = db.matchTitle(Id(initialID))

        val recommendations = mode match {
          case "votes" => engine.recommend(n.toInt, Id(initialID))
          case "tags" => engine.recommendByTags(n.toInt, Id(initialID))
          case _ => throw new Exception("Invalid mode: " + mode)
        }

        println("Recommendations for " + initialTitle + ":")
        for (rec <- recommendations) {
          println(s"${Id(rec.id)}: ${db.matchTitle(rec.id)} (${(rec.strength * 100).round / 100.0})")
          for ((tag, vote) <- db.getTags(rec.id).take(3)) {
            println(s" - $tag (${(vote * 100).round / 100.0})")
          }
        }

      case _ => println("Invalid arguments\n" +
        "Usage:\n" +
        "search NAME\n" +
        "recommend votes|tags COUNT ID")
    }

    sc.stop()
  }
}
