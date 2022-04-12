package carbon.vnrec.recommendation

import carbon.vnrec.db.Id.IdType

class Recommendation(val id: IdType,
                     val strength: Double)
  extends Serializable
