package carbon.vnrec

import carbon.vnrec.db.Vndb
import org.apache.spark.graphx.{Edge, Graph}

class Recommendation(val id: String,
                     val strength: Double)

class RecommendationEngine(private val db: Vndb) {
  def recommend(n: Int, initialID: String): Array[Recommendation] = {
    val vertices = db.vn
      .map(_.id)
      .keyBy(_.hashCode.toLong)
      .mapValues(vid => if (vid == initialID) 1.0 else 0.0)
      .union(
        db.users
          .map(_.id)
          .keyBy(_.hashCode.toLong)
          .mapValues(_ => 0.0)
      )

    val edges = db.ulist_vns
      .map(uvn => new Edge(
        uvn.uid.hashCode.toLong,
        uvn.vid.hashCode.toLong,
        uvn.vote / 100.0
      ))

    val credibility = Graph(vertices, edges)
      .aggregateMessages[Double](
        triple => triple.sendToSrc(triple.dstAttr * triple.attr),
        _ + _
      )

    val biasedRatings = Graph(credibility, edges)
      .aggregateMessages[(Double, Double)](
        triple => triple.sendToDst((triple.srcAttr * triple.attr, triple.srcAttr)),
        (a, b) => (a._1 + b._1, a._2 + b._2)
      )
      .mapValues(x => x._1 / x._2 * math.log(x._2))
      .filter(!_._2.isNaN)

    biasedRatings
      .join(
        db.vn
          .map(_.id)
          .filter(_ != initialID)
          .keyBy(_.hashCode.toLong)
      )
      .map(_._2)
      .top(n)
      .map(x => new Recommendation(x._2, x._1))
  }
}
