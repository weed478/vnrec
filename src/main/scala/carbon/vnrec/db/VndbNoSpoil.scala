package carbon.vnrec.db

import carbon.vnrec.db.Id.IdType
import org.apache.spark.rdd.RDD

class VndbNoSpoil(db: Vndb) extends Vndb {
  override def vn: RDD[Vn] = db.vn

  override def vn_titles: RDD[VnTitle] = db.vn_titles

  override def users: RDD[User] = db.users

  override def ulist_vns: RDD[UserVn] = db.ulist_vns

  override def tags: RDD[Tag] = db.tags

  override def tags_vn: RDD[TagVn] = db.tags_vn

  override def getTags(vid: IdType): RDD[(String, Double)] =
    normalizedTagVotes
      .filter(_.vid == vid)
      .keyBy(_.tag)
      .mapValues(_.vote)
      .join(tagSpoilerRatings
        .filter(_._1._2 == vid)
        .keyBy(_._1._1)
        .mapValues(_._2))
      .filter(_._2._2 < 0.5)
      .mapValues(_._1)
      .join(tags.keyBy(_.id).mapValues(_.name))
      .values
      .map(_.swap)
      .sortBy(_._2, ascending = false)
}
