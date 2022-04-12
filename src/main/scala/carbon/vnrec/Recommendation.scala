package carbon.vnrec

import carbon.vnrec.db.Id.IdType

class Recommendation(val id: IdType,
                     val strength: Double)
  extends Serializable
