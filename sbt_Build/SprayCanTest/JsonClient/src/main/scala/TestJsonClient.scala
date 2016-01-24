import akka.actor.ActorSystem
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import spray.http._
import spray.client.pipelining._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

import FacebookProtocol.User

object ClientApp{
  val apiLocation = "http://localhost:8080" //Make sure robotsAPI is running here

  val timeout = 5.seconds

  //Spray needs an implicit ActorSystem and ExecutionContext
  implicit val system = ActorSystem("robotClient")
  import system.dispatcher

  def getRobots() = {
    println("getting all users")
    val pipeline: HttpRequest => Future[List[User]] = sendReceive ~> unmarshal[List[User]]
    val f: Future[List[User]] = pipeline(Get(s"$apiLocation/users"))
    val users = Await.result(f, timeout)
    println(s"Got the list of robots: $users")
  }

  def postRobot() = {
    /*val newRobot = Robot("Data", Some("white"), 2)
    println("posting a new robot")
    val pipeline: HttpRequest => Future[Robot] = sendReceive ~> unmarshal[Robot]
    val f: Future[Robot] = pipeline(Post(s"$apiLocation/robots", newRobot))*/
    val newUser = User(3,"dlks","wew","wje")
    val pipeline: HttpRequest => Future[User] = sendReceive ~> unmarshal[User]
    val f: Future[User] = pipeline(Post(s"$apiLocation/user", newUser))
    val robot = Await.result(f, timeout).id
    println(s"got response : $robot")
  }

  def acceptRequest() ={
    val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
    val f: Future[String] = pipeline(Post(s"$apiLocation/addfriend/8/3"))
    val response = Await.result(f, timeout)
    println(response)
  }
  acceptRequest
  //postRobot()
  //getRobots()

  system.shutdown()
}
