package carbon.vnrec.server

import carbon.vnrec.VnRecommendationProvider
import scala.concurrent.Future
import carbon.vnrec.recommendation.Recommendation
import akka.http.caching.LfuCache
import akka.http.caching.scaladsl.Cache
import akka.actor.typed.ActorSystem
import scala.concurrent.ExecutionContext

class RecommendationCache(private val provider: VnRecommendationProvider)(
    implicit
    val system: ActorSystem[Any],
    val ec: ExecutionContext
) {
  val cache: Cache[(Int, Long), Array[Recommendation]] = LfuCache(
    system.classicSystem
  )

  def recommendFuture(n: Int, initialID: Long): Future[Array[Recommendation]] =
    Future { provider.recommend(n, initialID) }

  def recommendCached(
      n: Int,
      initialID: Long
  ): Future[Array[Recommendation]] = cache.getOrLoad(
    (n, initialID),
    { case (n, initialID) => recommendFuture(n, initialID) }
  )
}
