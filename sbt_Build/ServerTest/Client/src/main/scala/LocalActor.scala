import akka.actor._

case object JustStop

class LocalActor(system : ActorSystem) extends Actor{
	val remote = context.actorSelection("akka.tcp://HelloRemoteSystem@10.34.81.172:2552/home/RemoteActor")
	var counter = 0

	def receive = {
		case "start" =>
			println("Starting to Communicate")
			remote ! "Hello from local actor"
		
		case msg: String =>
			println("local actor received : "+msg)
			if(counter<5){
				sender ! "Hello back"
				counter += 1
			}
			else{
				println("stopping remote")
				sender ! "stop"
			}
		case JustStop =>
			println("Stopping myself")
			system.shutdown()
	}
}


object Local extends App{
	val system = ActorSystem("LocalSystem")
	val localActor = system.actorOf(Props(new LocalActor(system)), name = "LocalActor")
	localActor ! "start"
}

