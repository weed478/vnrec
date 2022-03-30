package carbon.vnrec.db

case class VnTitle(private val args: Array[String]) {
  val id: String = args(0)
  val lang: String = args(1)
  val title: String = args(2)
  val latin: String = args(3)
  val official: Boolean = args(4) == "t"

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
