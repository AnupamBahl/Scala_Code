import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.actor._
import spray.http.HttpMethods.{GET}
import spray.can.Http
import spray.http.{HttpRequest, HttpResponse, Uri}
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import spray.can.Http
import spray.can.server.Stats
import spray.util._
import spray.http._
import HttpMethods._
import MediaTypes._
import spray.can.Http.RegisterChunkHandler

object Main extends App {
  implicit val system = ActorSystem()

  // the handler actor replies to incoming HttpRequests
  val handler = system.actorOf(Props[WebService], name = "handler")

  IO(Http) ! Http.Bind(handler, interface = "localhost", port = 8080)
}

class WebService extends Actor with ActorLogging {
  implicit val timeout: Timeout = 1.second // for the actor 'asks'
  import context.dispatcher // ExecutionContext for the futures and scheduler

    def receive = {
      // New connection - register self as handler
      case _: Http.Connected =>
        sender ! Http.Register(self)
        println("connected")

      case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
        sender ! HttpResponse(entity = "pong")
        println("received ping")

      case HttpRequest(GET, Uri.Path("/pong"), _, _, _) =>
        sender ! HttpResponse(entity = "ping")
        println("received pong")


      case _: HttpRequest =>
        sender ! HttpResponse(status = 404, entity = "Unknown resource!")
        println("unknown request")


    }
}
