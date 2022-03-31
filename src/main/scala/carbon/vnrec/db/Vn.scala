package carbon.vnrec.db

object Vn {
  private def toOption(s: String): Option[String] = s match {
    case "\\N" => None
    case _ => Some(s)
  }

  def apply(row: String): Vn = {
    val args = row.split('\t')
    new Vn(
      args(0),
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

case class Vn private (id: String,
                       olang: String,
                       image: String,
                       l_wikidata: Option[String],
                       c_votecount: Int,
                       c_popularity: Int,
                       c_rating: Option[Int],
                       length: Int,
                       alias: Option[String],
                       l_renai: Option[Int],
                       desc: String,
                       c_average: Option[Int])
