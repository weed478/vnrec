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

      case Array("rec", initialID, n) =>
        val initialTitle = db.matchTitle(Id(initialID))

        val recommendations = engine
          .recommend(n.toInt, Id(initialID))

        println("Recommendations for " + initialTitle + ":")
        for (rec <- recommendations) {
          println(s"${Id(rec.id)}: ${db.matchTitle(rec.id)} (${(rec.strength * 100).round / 100.0})")
          val vid = rec.id
          val tags = db.tags_vn
            .filter(_.vid == vid)
            .keyBy(_.tag)
            .mapValues(_ => 1L)
            .reduceByKey(_ + _)
            .join(db.tags.keyBy(_.id))
            .map(t => (t._2._2.name, t._2._1))
            .sortBy(_._2, ascending = false)
            .take(3)
          for (tag <- tags) {
            println(s" - ${tag._1} (${tag._2})")
          }
        }

      case _ => println("Invalid arguments\n" +
        "Usage:\n" +
        "search NAME\n" +
        "rec ID COUNT")
    }

    sc.stop()
  }
}
