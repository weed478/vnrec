package carbon.vnrec.server

import carbon.vnrec.VnRecommendationProvider
import carbon.vnrec.recommendation.Recommendation
import carbon.vnrec.VnQueryProvider

import scala.collection.Iterator

class MockVnQueryProvider extends VnQueryProvider {
  private val db = Map(
    0L -> "A",
    1L -> "B",
    2L -> "C",
    3L -> "D",
    4L -> "E",
    5L -> "F",
    6L -> "G",
    7L -> "H"
  )

  def matchTitle(vid: Long): Option[String] = db
    .filter { case (id, s) => vid == id }
    .map { case (id, s) => s }
    .headOption

  def search(pattern: String): Array[Long] =
    db.filter { case (id, s) => pattern == s }
      .map { case (id, s) => id }
      .toArray
}

class MockVnRecommendationProvider extends VnRecommendationProvider {
  def recommend(n: Int, initialID: Long): Array[Recommendation] =
    Iterator
      .iterate(initialID + 1)(id => (id + 1) % 8)
      .map(id => new Recommendation(id, id))
      .take(n)
      .toArray
}
