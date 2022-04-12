package carbon.vnrec.recommendation

import carbon.vnrec.db.Id.IdType
import org.apache.spark.graphx.{Edge, Graph, VertexRDD}

trait VoteRecommendations extends RecommendationBase {

  protected def recommendByVotes(initialID: IdType): VertexRDD[Double] = {
    val vertices = initialVnWith(db.users.map(_.id), initialID)

    val edges = db.ulist_vns
      .map(uvn => new Edge(
        uvn.uid,
        uvn.vid,
        uvn.vote / 100.0
      ))

    val credibility = aggregateIntoDst(Graph(vertices, edges).reverse)

    val biasedRatings = Graph(credibility, edges)
      .aggregateMessages[(Double, Double)](
        triple => triple.sendToDst((triple.srcAttr * triple.attr, triple.srcAttr)),
        (a, b) => (a._1 + b._1, a._2 + b._2)
      )
      .mapValues(x => x._1 / x._2 * math.log(x._2))
      .filter(!_._2.isNaN)

    biasedRatings.filter(_._1 != initialID)
  }
}
