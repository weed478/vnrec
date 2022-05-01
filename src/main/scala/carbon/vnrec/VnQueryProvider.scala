package carbon.vnrec

import carbon.vnrec.db.Id.IdType

trait VnQueryProvider {
  def matchTitle(vid: IdType): Option[String]
  def search(pattern: String): Array[IdType]
}
