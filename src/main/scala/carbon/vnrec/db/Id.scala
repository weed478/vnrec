package carbon.vnrec.db

object Id {
  val prefixes: Array[Char] = Array(
    'v',
    'u',
    'g',
  )

  private val numIds = prefixes.length

  def apply(id: String): Long = {
    val offset = prefixes.iterator
      .takeWhile(_ != id(0))
      .length
    id.substring(1).toLong * numIds + offset
  }

  def apply(id: Long): String = {
    val offset = id % numIds
    prefixes(offset.toInt).toString + (id - offset) / numIds
  }
}
