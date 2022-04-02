package carbon.vnrec.server

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import scala.io.StdIn
import scala.io.Source
import carbon.vnrec.VnQueryProvider
import carbon.vnrec.VnRecommendationProvider
import carbon.vnrec.server.MockVnQueryProvider
import carbon.vnrec.Recommendation
import akka.http.scaladsl.server.StandardRoute

// Temporary
object HTTPServer {
  def main(args: Array[String]): Unit = {
    new HTTPServer(
      new MockVnQueryProvider(),
      new MockVnRecommendationProvider()
    ).serve()
  }
}

class HTTPServer(
    val vnQueryProvider: VnQueryProvider,
    val vnRecommendationProvider: VnRecommendationProvider
) {
  def serve() = {
    implicit val system = ActorSystem(Behaviors.empty, "server-actor-system")
    implicit val executionContext = system.executionContext

    val route =
      concat(
        path("") {
          get {
            redirect("/static/home.html", StatusCodes.PermanentRedirect)
          }
        },
        path("static" / "results.html") {
          parameters("vn", "type", "count".as[Int])(getResultsPage)
        },
        path("static" / Segment) { s =>
          get {
            readFile(s) match {
              case Some(content) =>
                complete(
                  HttpEntity(
                    if (s.endsWith(".css"))
                      ContentType(MediaTypes.`text/css`, HttpCharsets.`UTF-8`)
                    else if (s.endsWith(".html"))
                      ContentTypes.`text/html(UTF-8)`
                    else ContentTypes.`text/plain(UTF-8)`,
                    content
                  )
                )
              case None => getErrorPage("Invalid page.")
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
          case Some(id) if vnQueryProvider.isValidId(id) => id
          case Some(_) => return getErrorPage("Id not in the database.")
          case None    => return getErrorPage("Invalid id.")
        }
      else
        vnQueryProvider.search(vn).headOption match {
          case Some(id) => id
          case None     => return getErrorPage("Invalid title.")
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
