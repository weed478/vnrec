package server

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import scala.io.StdIn
import scala.io.Source

object HTTPServer {
  // Temporary
  def main(args: Array[String]): Unit = {
    serve()
  }

  def serve() = {
    implicit val system = ActorSystem(Behaviors.empty, "server-actor-system")
    implicit val executionContext = system.executionContext

    val route =
      concat(
        path("") {
          get {
            redirect("/static/main.html", StatusCodes.PermanentRedirect)
          }
        },
        path("static" / Segment) { s =>
          get {
            complete(
              HttpEntity(
                if (s.endsWith(".html")) ContentTypes.`text/html(UTF-8)`
                else ContentType(MediaTypes.`text/css`, HttpCharsets.`UTF-8`),
                readFile(s)
              )
            )
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

  def readFile(path: String): String = {
    val file = Source.fromFile(f"static/$path")
    var content =
      try file.mkString
      finally file.close()
    content
  }
}
