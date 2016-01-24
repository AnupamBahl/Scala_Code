import scala.concurrent.Future
import scala.concurrent.duration._
import akka.io.IO
import akka.util.Timeout
import akka.pattern.ask
import akka.actor._
import spray.can.Http
import spray.http._
import HttpMethods._

object ObjectMain{
  private implicit val timeout: Timeout = 5.seconds
  def main(args : Array[String]){
    val host = "spray.io"
    implicit val system = ActorSystem("ClientSystem")
    import system.dispatcher
    println("here")
    val result = requestActor(host)
    //system.shutdown()
  }

  def requestActor(host : String)(implicit system : ActorSystem): Future[ProductVersion] ={
    val actor = system.actorOf(Props(new MyRequestActor(host)), name = "my-request-actor")
    println("now here")
    val future = actor ? HttpRequest(GET, "/users")
    future.mapTo[ProductVersion]
  }
}


class MyRequestActor(host: String) extends Actor with ActorLogging {
  import context.system
  def receive: Receive = {
      case request: HttpRequest =>
        // start by establishing a new HTTP connection
        println("Connecting")
        IO(Http) ! Http.Connect("127.0.0.1", port = 8080)
        context.become(connecting(sender, request))
  }

  def connecting(commander: ActorRef, request: HttpRequest): Receive = {
      case _: Http.Connected =>
        // once connected, we can send the request across the connection
        println("Requesting")
        sender ! request
        context.become(waitingForResponse(commander))
  }

  def waitingForResponse(commander: ActorRef): Receive = {
      case response@ HttpResponse(status, entity, _, _) =>
        printf("got response : %s\n", entity.asString)
        log.info("Connection-Level API: received {} response with {} bytes", status, entity.data.length)
        system.shutdown()
  }
}
