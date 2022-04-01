package carbon.vnrec

import carbon.vnrec.db.Vndb
import org.apache.spark.graphx.{Edge, Graph}

class Recommendation(val id: Long,
                     val strength: Double)

class RecommendationEngine(private val db: Vndb) {
  def recommend(n: Int, initialID: Long): Array[Recommendation] = {
    val vertices = db.vn
      .map(vn => (vn.id, if (vn.id == initialID) 1.0 else 0.0))
      .union(db.users
        .map(user => (user.id, 0.0))
      )

    val edges = db.ulist_vns
      .map(uvn => new Edge(
        uvn.uid,
        uvn.vid,
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
          .keyBy(identity)
      )
      .map(_._2)
      .top(n)(Ordering.by(_._1))
      .map(x => new Recommendation(x._2, x._1))
  }
}
