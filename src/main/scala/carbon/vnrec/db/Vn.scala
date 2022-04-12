package carbon.vnrec.db

object Vn {
  private def toOption(s: String): Option[String] = s match {
    case "\\N" => None
    case _ => Some(s)
  }

  def apply(row: String): Vn = {
    val args = row.split('\t')
    new Vn(
      Id(args(0)),
      args(1),
      args(2),
      toOption(args(3)),
      args(4).toInt,
      args(5).toInt,
      args(6).toIntOption,
      args(7).toInt,
      toOption(args(8)),
      args(9).toIntOption,
      args(10),
      args(11).toIntOption,
    )
  }
}

class Vn private (val id: Long,
                  val olang: String,
                  val image: String,
                  val l_wikidata: Option[String],
                  val c_votecount: Int,
                  val c_popularity: Int,
                  val c_rating: Option[Int],
                  val length: Int,
                  val alias: Option[String],
                  val l_renai: Option[Int],
                  val desc: String,
                  val c_average: Option[Int])
  extends Serializable
