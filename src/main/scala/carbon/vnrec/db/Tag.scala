package carbon.vnrec.db

import carbon.vnrec.db.Id.IdType

object Tag {
  def apply(row: String): Tag = {
    val args = row.split('\t')
    new Tag(
      Id(args(0)),
      args(5),
      args(1) match {
        case "cont" => ContentTag
        case "tech" => TechnicalTag
        case "ero" => EroTag
        case other => OtherTag(other)
      },
    )
  }
}

sealed trait TagCat
case object ContentTag extends TagCat
case object TechnicalTag extends TagCat
case object EroTag extends TagCat
case class OtherTag(cat: String) extends TagCat

class Tag private (val id: IdType,
                   val name: String,
                   val cat: TagCat)
  extends Serializable
