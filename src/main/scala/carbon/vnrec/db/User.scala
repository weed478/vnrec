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

class User private (val id: String,
                    val ign_votes: Boolean,
                    val perm_imgvote: Boolean,
                    val perm_tag: Boolean,
                    val username: String,
                    val perm_lengthvote: Boolean)
