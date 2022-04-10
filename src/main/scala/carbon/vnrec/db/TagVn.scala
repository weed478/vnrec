package carbon.vnrec.db

object TagVn {
  def apply(row: String): TagVn = {
    val args = row.split('\t')
    new TagVn(
      Id(args(0)),
      Id(args(1)),
    )
  }
}

class TagVn private (val tag: Long,
                     val vid: Long)
extends Serializable
