package carbon.vnrec.db

object UserVn {
  def apply(row: String): Option[UserVn] = {
    val args = row.split('\t')
    args(7).toIntOption
      .map(vote => new UserVn(
        args(0),
        args(1),
        vote,
      ))
  }
}

case class UserVn private (uid: String,
                           vid: String,
                           vote: Int)
