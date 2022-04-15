package carbon.vnrec.db

import org.apache.spark.rdd.RDD

class Vndb(data: DataProvider) {
  val vn: RDD[Vn] = data.vn
    .map(Vn(_))
    .cache()

  val vn_titles: RDD[VnTitle] = data.vn_titles
    .map(VnTitle(_))
    .cache()

  val users: RDD[User] = data.users
    .map(User(_))
    .cache()

  val ulist_vns: RDD[UserVn] = data.ulist_vns
    .flatMap(UserVn(_))
    .cache()

  val tags: RDD[Tag] = data.tags
    .map(Tag(_))
    .cache()

  val tags_vn: RDD[TagVn] = data.tags_vn
    .map(TagVn(_))
    .cache()

  val normalizedTagVotes: RDD[TagVn] = tags_vn
    .keyBy(t => (t.tag, t.vid))
    .mapValues(t => (t.vote, 1L))
    .reduceByKey((t1, t2) => (t1._1 + t2._1, t1._2 + t2._2))
    .mapValues(t => t._1 / t._2 * Math.log(t._2))
    .filter(!_._2.isNaN)
    .map(x => new TagVn(x._1._1, x._1._2, x._2))
    .cache()

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
