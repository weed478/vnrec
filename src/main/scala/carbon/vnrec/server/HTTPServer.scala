package carbon.vnrec.server

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import scala.io.StdIn
import scala.io.Source
import carbon.vnrec.{LocalSpark, BackendSystem, VnQueryProvider, VnRecommendationProvider}
import carbon.vnrec.server.MockVnQueryProvider
import carbon.vnrec.recommendation.Recommendation
import akka.http.scaladsl.server.StandardRoute
import spray.json._
import DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import carbon.vnrec.db.{DirectoryDataProvider, Id}

// Temporary
object HTTPServer {
  def main(args: Array[String]): Unit = {
    val sc = LocalSpark.getOrCreate()
    val data = new DirectoryDataProvider(sc, "data")
    val backend = new BackendSystem(data)
    new HTTPServer(backend, backend)
      .serve()
    sc.stop()
  }
}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val recommendationFormat = jsonFormat2(Recommendation)
}

class HTTPServer(
    val vnQueryProvider: VnQueryProvider,
    val vnRecommendationProvider: VnRecommendationProvider
) extends JsonSupport {
  def serve() = {
    implicit val system = ActorSystem(Behaviors.empty, "server-actor-system")
    implicit val executionContext = system.executionContext

    val route =
      concat(
        path("") {
          get {
            redirect("/home", StatusCodes.PermanentRedirect)
          }
        },
        pathPrefix("api") {
          concat(
            path("search" / Segment) { s =>
              get {
                complete(vnQueryProvider.search(s).toJson)
              }
            },
            path("recommend" / Segment / Segment) { (count, id) =>
              get {
                complete(
                  vnRecommendationProvider.recommend(count.toInt, Id(id)).toJson
                )
              }
            }
          )
        },
        path("results") {
          parameters("vn", "type", "count".as[Int])(getResultsPage)
        },
        path(Segment) { s =>
          get {
            val (content_type, file_name) =
              if (s.endsWith(".css"))
                (ContentType(MediaTypes.`text/css`, HttpCharsets.`UTF-8`), s)
              else
                (ContentTypes.`text/html(UTF-8)`, f"$s.html")

            readFile(file_name) match {
              case Some(content) => complete(HttpEntity(content_type, content))
              case None          => getErrorPage("Invalid page.")
            }
          }
        }
      )

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(
      s"\n\nServer now online [http://localhost:8080] \nPress RETURN to stop..."
    )

    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  def getResultsPage(vn: String, tpe: String, count: Int): StandardRoute = {
    val vnId =
      if (tpe == "id")
        vn.toLongOption match {
          case Some(id) if vnQueryProvider.matchTitle(id).isDefined => id
          case Some(_) => return getErrorPage("Id not in the database.")
          case None    => return getErrorPage("Invalid id.")
        }
      else
        vnQueryProvider.search(vn).headOption match {
          case Some(id) => id
          case None     => return getErrorPage("Title not in the database.")
        }

    val recommendations = vnRecommendationProvider.recommend(count, vnId)

    val recommendationsText =
      recommendations
        .map { rec =>
          f"${vnQueryProvider.matchTitle(rec.id).get} [${rec.strength}]"
        }
        .mkString(" <br /> ")

    val fileContent =
      readFile("results.html").get.replace("{{}}", recommendationsText)

    complete(
      HttpEntity(
        ContentTypes.`text/html(UTF-8)`,
        fileContent
      )
    )
  }

  def getErrorPage(message: String): StandardRoute = {
    complete(
      HttpEntity(
        ContentTypes.`text/html(UTF-8)`,
        readFile("error.html").get.replace("{{}}", message)
      )
    )
  }

  def readFile(path: String): Option[String] = {
    try {
      val file = Source.fromFile(f"static/$path")
      try Some(file.mkString)
      catch { case _: Throwable => None }
      finally file.close()
    } catch { case _: Throwable => None }
  }
}
