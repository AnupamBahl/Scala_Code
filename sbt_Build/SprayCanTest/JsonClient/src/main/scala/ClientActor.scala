import akka.actor._
import akka.actor.ActorSystem
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import spray.http._
import spray.client.pipelining._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

import FacebookProtocol.User

class ClientActor(id : Int, system:ActorSystem) extends Actor{
  val selfId = id
  var selfName = "new"
  val serverLocation = "http://localhost:8080"
  val timeout = 3.seconds

  var failedAttempts = 0

  def receive = {
    case InitializeUser(name : String, userName : String) => {
        val newUser = User(selfId, name, userName, "")
        selfName = name
        println( createUser(newUser) )
        //createUser(newUser)
        //sender ! UserCreated
    }

    case AddFriend(friendId : Int) => {
      println( stringCases("addfriend",selfId,friendId) )
      //stringCases("addfriend",selfId,friendId)
      //sender ! FriendsAdded
    }

    case AcceptRequest(friendId : Int) => {
      println( stringCases("acceptrequest", selfId, friendId) )
    }

    // Needs server changes
    case GetFriendsList => {
      println( getFriendsList("friendlist") )
    }

    case GetAnotherUserProfile(userId : Int) => {
      val response = getAnotherUserProfile("getprofile", userId)
      printf("User Name : %s\n", response.username)
    }

    case PostOnUserPage(userId : Int, postData : String) => {
      val newPost = FacebookProtocol.Post(selfName, postData, "")
      println( postOnUserPage("posting", userId, newPost) )
      //if(postOnUserPage("posting", userId, newPost)=="ERROR")
      //{
      //  failedAttempts+=1
      //}
    }

    case GetUserWallPage(userId : Int) => {
      println( getUserWallPage("page", userId) )
    }

    case AcceptAllRequests => {
      val str = "acceptallrequests"
      //acceptAllRequests(str)
      println( acceptAllRequests(str) )
      //sender ! AcceptedRequests
    }

    case ReturnErrors => {
      sender ! ReceiveErrorsCount(failedAttempts)
    }

  }

  import system.dispatcher
  def stringCases(str : String, idOne : Int, idTwo : Int) : String = {
    var response = ""
    try{
      val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
      val f: Future[String] = pipeline(Post(s"$serverLocation/$str/$idOne/$idTwo"))
      response = Await.result(f, timeout)
    } catch{
      case e: Exception =>
        //printf("Failed with exception : %s\n",e)
        failedAttempts += 1
    }
    return response
  }

  def createUser(newUser : User) : String = {
    var response = ""
    try{
      val timot = 5.seconds
      val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
      val f: Future[String] = pipeline(Post(s"$serverLocation/user", newUser))
      response = Await.result(f, timot)
    } catch{
      case e: Exception =>
        failedAttempts += 1
        //printf("Failed create with exception : %s\n",e)
    }
    return response+" Created"
  }

  // What to expect?????
  def getFriendsList(str : String) : String = {
    val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
    val f: Future[String] = pipeline(Get(s"$serverLocation/$str/$selfId"))
    val response = Await.result(f, timeout)
    return response
  }

  def getAnotherUserProfile(str : String, userId : Int) : User = {
    var response = User(0,"Fake","Fake","")
    try{
      val pipeline: HttpRequest => Future[User] = sendReceive ~> unmarshal[User]
      val f: Future[User] = pipeline(Get(s"$serverLocation/$str/$userId"))
      response = Await.result(f, timeout)
    } catch{
      case e: Exception =>
        failedAttempts += 1
        //printf("Failed get profile with exception : %s\n",e)
    }
    return response
  }

  def postOnUserPage(str : String, receiverId : Int, newpost : FacebookProtocol.Post) : String = {
    var response = ""
    try{
      val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
      val f: Future[String] = pipeline(Post(s"$serverLocation/$str/$selfId/$receiverId/1", newpost))
      response = Await.result(f, timeout)
    } catch{
      case e: Exception =>
        failedAttempts += 1
        //printf("Failed Post with exception : %s\n",e)
    }
    return response
  }

  def getUserWallPage(str : String, receiverId : Int) : String = {
    var response = ""
    try{
      val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
      val f: Future[String] = pipeline(Get(s"$serverLocation/$str/1/$selfId/$receiverId"))
      response = Await.result(f, timeout)
    } catch{
      case e: Exception =>
        failedAttempts += 1
        //printf("Failed Get user wall with exception : %s\n",e)
    }
    return response
  }

  def acceptAllRequests(str : String) : String = {
    val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
    val f: Future[String] = pipeline(Post(s"$serverLocation/$str/$selfId"))
    val response = Await.result(f, timeout)
    return response
  }

}
