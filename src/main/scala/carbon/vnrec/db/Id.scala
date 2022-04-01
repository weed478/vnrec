package carbon.vnrec.db

object Id {
  private val numIds = 2

  def apply(id: String): Long = id(0) match {
    case 'v' => id.substring(1).toLong * numIds
    case 'u' => id.substring(1).toLong * numIds + 1
    case _ => throw new Exception("Invalid ID: " + id)
  }

  def apply(id: Long): String = id % numIds match {
    case 0 => "v" + id / numIds
    case 1 => "u" + (id - 1) / numIds
    case _ => throw new Exception("Invalid ID: " + id)
  }
}
