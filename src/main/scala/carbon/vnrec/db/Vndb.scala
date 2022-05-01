package carbon.vnrec.db

import carbon.vnrec.db.Id.IdType
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

trait Vndb {
  def vn: RDD[Vn]

  def vn_titles: RDD[VnTitle]

  def users: RDD[User]

  def ulist_vns: RDD[UserVn]

  def tags: RDD[Tag]

  def tags_vn: RDD[TagVn]

  def normalizedTagVotes: RDD[TagVn] = tags_vn
    .keyBy(t => (t.tag, t.vid))
    .mapValues(t => (t.vote, 1L))
    .reduceByKey((t1, t2) => (t1._1 + t2._1, t1._2 + t2._2))
    .mapValues(t => t._1 / t._2 * Math.log(t._2))
    .filter(!_._2.isNaN)
    .map(x => new TagVn(x._1._1, x._1._2, x._2, None))
    .cache()

  def tagSpoilerRatings: RDD[((IdType, IdType), Double)] =
    tags_vn
      .keyBy(t => (t.tag, t.vid))
      .mapValues(t => (t.spoiler match {
        case Some(MajorSpoiler) => 3.0
        case Some(MinorSpoiler) => 2.0
        case Some(NoSpoiler) => 1.0
        case _ => -1.0
      }, 1L))
      .filter(_._2._1 > 0)
      .reduceByKey((t1, t2) => (t1._1 + t2._1, t1._2 + t2._2))
      .mapValues(t => t._1 / t._2)
      .cache()

  def getTags(vid: IdType): RDD[(String, Double)] =
    normalizedTagVotes
      .filter(_.vid == vid)
      .keyBy(_.tag)
      .mapValues(_.vote)
      .join(tags.keyBy(_.id).mapValues(_.name))
      .values
      .map(_.swap)

  def joinToTags[T: ClassTag](vid: T => IdType)(data: RDD[T]): RDD[(T, Seq[(String, Double)])] =
    data
      .keyBy(vid)
      .join(normalizedTagVotes.keyBy(_.vid))
      .values
      .keyBy(_._2.tag)
      .mapValues { case (t, tvn) => (t, tvn.vote) }
      .join(tags.keyBy(_.id).mapValues(_.name))
      .values
      .map { case ((t, strength), tag) => (t, Seq((tag, strength))) }
      .keyBy(x => vid(x._1))
      .reduceByKey((a, b) => (a._1, a._2 ++ b._2))
      .values

  def matchTitle(vid: IdType): String = {
    vn_titles
      .filter(_.id == vid)
      .map(_.bestTitle)
      .reduce((t1, t2) => if (t1._1 < t2._1) t2 else t1)
      ._2
  }

  def search(pattern: String): RDD[IdType] = {
    vn_titles
      .filter(title => {
        title.title.toLowerCase.contains(pattern.toLowerCase) ||
          title.latin.toLowerCase.contains(pattern.toLowerCase())
      })
      .map(_.id)
      .distinct
  }

  def safe: Vndb = new VndbSafe(this)

  def noSpoil: Vndb = new VndbNoSpoil(this)
}
