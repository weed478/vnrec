package carbon.vnrec.db

object TagVn {
  def apply(row: String): TagVn = {
    val args = row.split('\t')
    new TagVn(
      Id(args(0)),
      Id(args(1)),
      args(3).toDouble
    )
  }
}

class TagVn (val tag: Long,
             val vid: Long,
             val vote: Double)
extends Serializable
