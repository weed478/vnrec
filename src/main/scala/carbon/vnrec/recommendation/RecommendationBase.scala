package carbon.vnrec.recommendation

import carbon.vnrec.db.Id.IdType
import carbon.vnrec.db.Vndb
import org.apache.spark.graphx.{Graph, VertexRDD}
import org.apache.spark.rdd.RDD

trait RecommendationBase {

  protected val db: Vndb

  protected def normalize(data: RDD[(IdType, Double)]): RDD[(IdType, Double)] = {
    if (data.isEmpty()) data
    else {
      val max = data.values.max()
      data.mapValues(_ / max)
    }
  }

  protected def aggregateIntoDst(graph: Graph[Double, Double]): VertexRDD[Double] =
    graph.aggregateMessages[Double](
      e => e.sendToDst(e.srcAttr * e.attr),
      _ + _
    )

  protected def initialVnWith(other: RDD[IdType], initialId: IdType): VertexRDD[Double] =
    VertexRDD(db.vn
      .map(vn => (vn.id, if (vn.id == initialId) 1.0 else 0.0))
      .union(other.map(k => (k, 0.0)))
    )
}
