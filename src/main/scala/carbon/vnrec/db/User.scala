package carbon.vnrec.db

object User {
  def apply(row: String): User = {
    val args = row.split('\t')
    new User(
      args(0),
      args(1) == "t",
      args(2) == "t",
      args(3) == "t",
      args(4),
      args(5) == "t",
    )
  }
}

case class User private (id: String,
                         ign_votes: Boolean,
                         perm_imgvote: Boolean,
                         perm_tag: Boolean,
                         username: String,
                         perm_lengthvote: Boolean)
