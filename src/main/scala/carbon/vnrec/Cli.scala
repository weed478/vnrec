package carbon.vnrec

import carbon.vnrec.db.{DirectoryDataProvider, Id, VndbRaw}
import carbon.vnrec.recommendation.RecommendationEngine

object Cli {
  def main(args: Array[String]): Unit = {
    val sc = LocalSpark.getOrCreate()
    val data = new DirectoryDataProvider(sc, "data")
    val db = new VndbRaw(data)
    val engine = new RecommendationEngine(db)

    val safe = args.take(2).contains("safe")
    val nospoil = args.take(2).contains("nospoil")

    val filteredDb = {
      val safeDb = if (safe) db.safe else db
      if (nospoil) safeDb.noSpoil else safeDb
    }

    args.dropWhile(a => a == "safe" || a == "nospoil") match {
      case Array("search", pattern) =>
        db.search(pattern).collect.foreach(vid => {
          println(Id(vid) + ": " + db.matchTitle(vid))
        })

      case Array("recommend", count, initialID) =>
        val initialTitle = db.matchTitle(Id(initialID))

        val recommendations = engine
          .recommend(Id(initialID))
          .take(count.toInt)

        println("Recommendations for " + initialTitle + ":")
        for (rec <- recommendations) {
          println(s"${Id(rec.id)}: ${db.matchTitle(rec.id)} (${(rec.strength * 100).round / 100.0})")
          for ((tag, vote) <- filteredDb.getTags(rec.id).take(3)) {
            println(s" - $tag (${(vote * 100).round / 100.0})")
          }
        }

      case _ => println("Invalid arguments\n" +
        "Usage:\n" +
        "[safe] [nospoil] search NAME\n" +
        "[safe] [nospoil] recommend COUNT ID")
    }

    sc.stop()
  }
}
