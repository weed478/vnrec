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

class UserVn private (val uid: String,
                      val vid: String,
                      val vote: Int)
