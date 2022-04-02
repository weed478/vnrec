package carbon.vnrec

trait VnQueryProvider {
  def matchTitle(vid: Long): Option[String]
  def search(pattern: String): Array[Long]
  def isValidId(vid: Long): Boolean
}
