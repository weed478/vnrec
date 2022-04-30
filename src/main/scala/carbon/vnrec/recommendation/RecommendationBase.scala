package carbon.vnrec.recommendation

import carbon.vnrec.db.Id.IdType
import carbon.vnrec.db.Vndb
import org.apache.spark.rdd.RDD

trait RecommendationBase {

  protected val db: Vndb

  protected def normalize(data: RDD[(IdType, Double)]): RDD[(IdType, Double)] = {
    data.cache()
    if (data.isEmpty()) data
    else {
      val max = data.values.max()
      data.mapValues(_ / max)
    }
  }
}
