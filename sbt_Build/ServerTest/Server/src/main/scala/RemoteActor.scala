import akka.actor._

case object JustStop

class RemoteActor(system : ActorSystem) extends Actor{
	def receive = {
		case "start" =>
			println("RemoteActor is alive")
		case msg:String =>
			if(msg != "stop"){
				println(s"RemoteActor received message '$msg'")
				sender ! "Hello from the remote Actor"
			}
			else{
				println("stopping local and self")
				sender ! JustStop
				system.shutdown()
			}
	}
}

object HelloRemote extends App{
	val system = ActorSystem("HelloRemoteSystem")
	val remoteActor = system.actorOf(Props(new RemoteActor(system)), "RemoteActor")
	remoteActor ! "start"
}

