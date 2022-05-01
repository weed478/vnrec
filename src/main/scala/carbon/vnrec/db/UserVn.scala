package carbon.vnrec.db

import carbon.vnrec.db.Id.IdType

object UserVn {
  def apply(row: String): Option[UserVn] = {
    val args = row.split('\t')
    args(7).toIntOption
      .map(vote => new UserVn(
        Id(args(0)),
        Id(args(1)),
        vote,
      ))
  }
}

class UserVn private (val uid: IdType,
                      val vid: IdType,
                      val vote: Int)
  extends Serializable
