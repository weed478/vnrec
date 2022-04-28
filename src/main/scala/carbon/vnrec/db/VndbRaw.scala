package carbon.vnrec.db

import org.apache.spark.rdd.RDD

class VndbRaw(data: DataProvider) extends Vndb {
  override def vn: RDD[Vn] = data.vn
    .map(Vn(_))
    .cache()

  override def vn_titles: RDD[VnTitle] = data.vn_titles
    .map(VnTitle(_))
    .cache()

  override def users: RDD[User] = data.users
    .map(User(_))
    .cache()

  override def ulist_vns: RDD[UserVn] = data.ulist_vns
    .flatMap(UserVn(_))
    .cache()

  override def tags: RDD[Tag] = data.tags
    .map(Tag(_))
    .cache()

  override def tags_vn: RDD[TagVn] = data.tags_vn
    .map(TagVn(_))
    .cache()
}
