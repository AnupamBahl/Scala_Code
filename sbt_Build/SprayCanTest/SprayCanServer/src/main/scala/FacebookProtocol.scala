package com.anupam.siddhant.facebook
import scala.collection.mutable.ArrayBuffer
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.routing._

object FacebookProtocol {
  
  import spray.json._
  
  case class User(id:Int , name:String , username:String , password:String)

  case class Post(id:Int, creator:String, pageid:Int , content:String)

  case class Page(id:Int,creator:String, posts: ArrayBuffer[Post])



  //case class Quiz(id: String, question: String, correctAnswer: String)
  
  case object UserCreated
  
  case object UserAlreadyExists
  
  case object UserDeleted

  case class FriendRequest(id:Int)

  case class RequestSent(id:Int)

  case class RequestAccepted(id:Int)

  case class AcceptRequest(id:Int)

  case class PageRequest(pageid:Int,userid:Int,ctx:RequestContext)

  var Users = Vector(User(1,"siddhant","siddhantd28","sid"), User(2,"anuj","anuj","@nuj") ) 
  /*
  
  case class Question(id: String, question: String)
  
  case object QuestionNotFound
  
  
  case class Answer(answer: String)
  
  case object CorrectAnswer
  
  case object WrongAnswer*/
  
  /* json (un)marshalling */
  
  object User extends DefaultJsonProtocol {
    implicit val format = jsonFormat4(User.apply)

    implicit val format = jsonFormat3(Page.apply)
  }
/*
  object Question extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Question.apply)
  }

  object Answer extends DefaultJsonProtocol {
    implicit val format = jsonFormat1(Answer.apply)
  }
  
  /* implicit conversions */

  implicit def toQuestion(quiz: Quiz): Question = Question(id = quiz.id, question = quiz.question)

  implicit def toAnswer(quiz: Quiz): Answer = Answer(answer = quiz.correctAnswer)
}
*/

}