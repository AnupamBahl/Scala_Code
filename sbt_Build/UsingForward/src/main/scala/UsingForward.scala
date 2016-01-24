import akka.actor._

object ObjectMain{

  def main(args:Array[String]){
    val system = ActorSystem("system")
    val actorThree = system.actorOf(Props[ActorThree], "ActorThree")
    val actorTwo = system.actorOf(Props(new ActorTwo(actorThree)), "ActorTwo")
    val actorOne = system.actorOf(Props(new ActorOne(actorTwo)), "ActorOne")

    actorOne ! Start
    Thread sleep 2000
    system.shutdown()
  }
}
