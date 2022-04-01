package carbon.vnrec.db

object VnTitle {
  def apply(row: String): VnTitle = {
    val args = row.split('\t')
    new VnTitle(
      Id(args(0)),
      args(1),
      args(2),
      args(3),
      args(4) == "t",
    )
  }
}

class VnTitle private (val id: Long,
                       val lang: String,
                       val title: String,
                       val latin: String,
                       val official: Boolean) {
  def bestTitle: (Int, String) = {
    if (official) {
      if (lang == "en") (0, title) // official english
      else if (lang == "jp") {
        if (latin != "\\N") (-1, latin) // official jp latin
        else (-2, title) // official jp
      }
      else if (latin != "\\N") (-3, latin) // official other latin
      else (-4, title) // official other
    }
    else (-5, title) // unofficial
  }
}
