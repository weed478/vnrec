package carbon.vnrec.db

import org.apache.spark.rdd.RDD

trait DataProvider {

  val vn: RDD[String]

  val vn_titles: RDD[String]

  val users: RDD[String]

  val ulist_vns: RDD[String]

  val tags: RDD[String]

  val tags_vn: RDD[String]
}
