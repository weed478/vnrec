package carbon.vnrec.recommendation

import carbon.vnrec.db.Id.IdType

case class Recommendation(val id: IdType, val strength: Double)
    extends Serializable
