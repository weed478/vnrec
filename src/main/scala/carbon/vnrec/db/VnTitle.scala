package carbon.vnrec.db

object VnTitle {
  def apply(row: String): VnTitle = {
    val args = row.split('\t')
    new VnTitle(
      args(0),
      args(1),
      args(2),
      args(3),
      args(4) == "t",
    )
  }
}

case class VnTitle private (id: String,
                            lang: String,
                            title: String,
                            latin: String,
                            official: Boolean) {
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
