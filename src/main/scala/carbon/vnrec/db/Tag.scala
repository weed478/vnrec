package carbon.vnrec.db

object Tag {
  def apply(row: String): Tag = {
    val args = row.split('\t')
    new Tag(
      Id(args(0)),
      args(5),
    )
  }
}

class Tag private (val id: Long,
                   val name: String)
extends Serializable
