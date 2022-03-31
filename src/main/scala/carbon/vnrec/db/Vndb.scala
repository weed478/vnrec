package carbon.vnrec.db

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

class Vndb(private val sc: SparkContext) {
  val vn: RDD[Vn] = sc
    .textFile("data/db/vn")
    .map(Vn(_))

  val vn_titles: RDD[VnTitle] = sc
    .textFile("data/db/vn_titles")
    .map(VnTitle(_))

  val users: RDD[User] = sc
    .textFile("data/db/users")
    .map(User(_))

  val ulist_vns: RDD[UserVn] = sc
    .textFile("data/db/ulist_vns")
    .map(UserVn(_))
    .filter(_.isDefined)
    .map(_.get)

  def matchTitle(vid: String): String = {
    vn_titles
      .filter(_.id == vid)
      .map(_.bestTitle)
      .reduce((t1, t2) => if (t1._1 < t2._1) t2 else t1)
      ._2
  }
}
