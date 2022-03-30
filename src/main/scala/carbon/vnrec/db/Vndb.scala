package carbon.vnrec.db

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

class Vndb(private val sc: SparkContext) {
  val vn: RDD[String] = sc
    .textFile("data/db/vn")
    .map(_.split('\t')(0))

  val vn_titles: RDD[VnTitle] = sc
    .textFile("data/db/vn_titles")
    .map(row => VnTitle(row.split('\t')))

  val users: RDD[String] = sc
    .textFile("data/db/users")
    .map(_.split('\t')(0))

  val ulist_vns: RDD[String] = sc
    .textFile("data/db/ulist_vns")

  def matchTitle(vid: String): String = {
    vn_titles
      .filter(_.id == vid)
      .map(_.bestTitle)
      .reduce((t1, t2) => if (t1._1 < t2._1) t2 else t1)
      ._2
  }
}
