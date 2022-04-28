package carbon.vnrec.db

import org.apache.spark.rdd.RDD

class VndbSafe(db: Vndb) extends Vndb {
  override def vn: RDD[Vn] = db.vn

  override def vn_titles: RDD[VnTitle] = db.vn_titles

  override def users: RDD[User] = db.users

  override def ulist_vns: RDD[UserVn] = db.ulist_vns

  override def tags: RDD[Tag] = db.tags
    .filter(_.cat != EroTag)

  override def tags_vn: RDD[TagVn] = db.tags_vn
}
