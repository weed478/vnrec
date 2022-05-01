package carbon.vnrec.db

object Id {
  type IdType = Long

  val prefixes: Array[Char] = Array(
    'v',
    'u',
    'g'
  )

  private val numIds = prefixes.length

  def apply(id: String): IdType = {
    val offset = prefixes.iterator
      .takeWhile(_ != id(0))
      .length
    id.substring(1).toLong * numIds + offset
  }

  def tryGet(id: String): Option[IdType] = {
    val offset = prefixes.iterator
      .takeWhile(_ != id(0))
      .length
    if (offset == numIds) None
    else id.substring(1).toLongOption.map(_ * numIds + offset)
  }

  def apply(id: IdType): String = {
    val offset = id % numIds
    prefixes(offset.toInt).toString + (id - offset) / numIds
  }
}
