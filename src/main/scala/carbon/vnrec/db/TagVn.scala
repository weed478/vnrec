package carbon.vnrec.db

object TagVn {
  def apply(row: String): TagVn = {
    val args = row.split('\t')
    new TagVn(
      Id(args(0)),
      Id(args(1)),
      args(3).toDouble,
      Some(args(4) match {
        case "2" => MajorSpoiler
        case "1" => MinorSpoiler
        case _ => NoSpoiler
      }),
    )
  }
}

sealed trait TagSpoiler
case object NoSpoiler extends TagSpoiler
case object MinorSpoiler extends TagSpoiler
case object MajorSpoiler extends TagSpoiler

class TagVn (val tag: Long,
             val vid: Long,
             val vote: Double,
             val spoiler: Option[TagSpoiler])
  extends Serializable
