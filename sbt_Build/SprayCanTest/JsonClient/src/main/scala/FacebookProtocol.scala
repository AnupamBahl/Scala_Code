
import scala.collection.mutable.ArrayBuffer
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.routing._

object FacebookProtocol {

  import spray.json._

  case class User(id:Int , name:String , username:String , password:String)

  case class Post(creator:String , content:String , time:String)

  case class Page(id:Int,creator:String, posts: Vector[Post])

  case class Group(id:Int,creator:String,name:String)


  //case class Quiz(id: String, question: String, correctAnswer: String)

  case object UserCreated

  case object UserAlreadyExists

  case object GroupCreated

  case object GroupAldreadyExists

  case object UserDeleted

  case class FriendRequest(id:Int)

  case class RequestSent(id:Int)

  case class RequestAccepted(id:Int)

  case class AcceptRequest(id:Int)

  case class PageRequest(pageid:Int,userid:Int,ctx:RequestContext)

  case class GetFriendList(ctx:RequestContext)

  case class AcceptAll(ctx:RequestContext)

  case class Posting(senderid:Int,pageid:Int,newpost:Post,ctx:RequestContext)

  case class GetProfile(ctx:RequestContext)

  case class JoinGroup(id:Int,ctx:RequestContext)

  case class GroupPosting(id:Int,newpost:Post,ctx:RequestContext)

  var Users = Vector(User(1,"siddhant","siddhantd28","sid"), User(2,"anuj","anuj","@nuj") )

  var Groups= Vector[Group]()

  var AllUsers = scala.collection.mutable.Map(1 -> "siddhant" , 2 -> "anuj")


  // json (un)marshalling

  object User extends DefaultJsonProtocol {
    implicit val userformat = jsonFormat4(User.apply)






  }

  object Post extends DefaultJsonProtocol{

    implicit val postformat = jsonFormat3(Post.apply)
  }

  object Group extends DefaultJsonProtocol{

    implicit val groupformat = jsonFormat3(Group.apply)
  }




}
