package carbon.vnrec

trait VnQueryProvider {
  def matchTitle(vid: Long): Option[String]
  def search(pattern: String): Array[Long]
}
