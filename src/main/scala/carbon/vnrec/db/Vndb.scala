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
    .flatMap(UserVn(_))

  val tags: RDD[Tag] = sc
    .textFile("data/db/tags")
    .map(Tag(_))

  val tags_vn: RDD[TagVn] = sc
    .textFile("data/db/tags_vn")
    .map(TagVn(_))

  val normalizedTagVotes: RDD[TagVn] = tags_vn
    .keyBy(t => (t.tag, t.vid))
    .mapValues(t => (t.vote, 1L))
    .reduceByKey((t1, t2) => (t1._1 + t2._1, t1._2 + t2._2))
    .mapValues(t => t._1 / t._2 * Math.log(t._2))
    .filter(!_._2.isNaN)
    .map(x => new TagVn(x._1._1, x._1._2, x._2))

  def getTags(vid: Long): RDD[(String, Double)] =
    normalizedTagVotes
      .filter(_.vid == vid)
      .keyBy(_.tag)
      .mapValues(_.vote)
      .join(tags.keyBy(_.id).mapValues(_.name))
      .values
      .map(_.swap)
      .sortBy(_._2, ascending = false)

  def matchTitle(vid: Long): String = {
    vn_titles
      .filter(_.id == vid)
      .map(_.bestTitle)
      .reduce((t1, t2) => if (t1._1 < t2._1) t2 else t1)
      ._2
  }

  def search(pattern: String): RDD[Long] = {
    vn_titles
      .filter(title => {
        title.title.toLowerCase.contains(pattern.toLowerCase) ||
          title.latin.toLowerCase.contains(pattern.toLowerCase())
      })
      .map(_.id)
      .distinct
  }
}
