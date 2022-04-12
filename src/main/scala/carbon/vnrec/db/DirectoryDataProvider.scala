package carbon.vnrec.db
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

class DirectoryDataProvider (sc: SparkContext,
                             path: String)
  extends DataProvider {

  override val vn: RDD[String] =
    file("vn")

  override val vn_titles: RDD[String] =
    file("vn_titles")

  override val users: RDD[String] =
    file("users")

  override val ulist_vns: RDD[String] =
    file("ulist_vns")

  override val tags: RDD[String] =
    file("tags")

  override val tags_vn: RDD[String] =
    file("tags_vn")

  private def file(name: String): RDD[String] = sc
    .textFile(s"$path/db/$name")
}
