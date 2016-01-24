import akka.actor._
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._

case class gotIt(str : String)
case object StopWork

object ObjectMain{

  def main(args : Array[String]){
    val system = ActorSystem("system")
    var actor = system.actorOf(Props(new TryActor(system)), "TryActor")
    actor ! gotIt("First")
    //Thread sleep 2000
    //system.shutdown()
  }

}

class ChildActor(mainStr : String) extends Actor{
  var num = 0
  def receive = {
    case gotIt(str : String) =>
    while(true){
      //printf("Child got : %s from : %s\n", str, mainStr)
      num += 1
      Thread sleep 200
    }
  }
}


  class ChildTwoActor(mainStr : String) extends Actor{
    val childActor = context.actorOf(Props(new ChildActor(mainStr)), "ChildActor")
    def receive = {
      case gotIt(str : String) =>
        printf("Child Two got : %s from : %s\n", str, mainStr)
        childActor ! gotIt("Start")

      case StopWork =>
        context.stop(childActor)
        context.stop(self)

    }
    override def postStop() = {
      println("Actor two stopped")
    }
  }

class TryActor(system : ActorSystem) extends Actor{
  var mainStr = ""
  val childTwoActor = context.actorOf(Props(new ChildTwoActor(mainStr)), "ChildTwoActor")

  context.watch(childTwoActor)

  def receive = {
    case gotIt(str : String) =>{
      mainStr = str
      printf("Actor got: %s \n",str)
      childTwoActor ! gotIt("Sent")
      Thread sleep 1000
      childTwoActor ! StopWork
    }

    case Terminated(terminateActorRef) =>
      println(s"Child Actor {$terminateActorRef} got terminated")
      system.shutdown
  }
}
