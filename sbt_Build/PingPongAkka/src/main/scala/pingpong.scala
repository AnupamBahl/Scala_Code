import akka.actor._

case object PingMessage
case object PongMessage
case object StopMessage
case object StartMessage


class Ping(pong : ActorRef) extends Actor{
	var counter = 0
	def incrementCounter():Int = {
		counter += 1
		counter 
	}
	
	def receive = {
		case StartMessage =>
			printf("Ping : %d", incrementCounter)
			pong ! PingMessage
		case PongMessage =>
			if(counter < 99){
				printf("Ping : %d", incrementCounter)
				pong ! PingMessage
			}
			else{
				printf("Ping : %d", incrementCounter)
				sender ! StopMessage
				context.stop(self)
			}
	}
}

class Pong(system:ActorSystem) extends Actor{
	def receive = {
		case PingMessage =>
			printf("\tPong in reply\n")
			sender ! PongMessage
		case StopMessage =>
			printf("\tLast Pong in reply\n\n\n")
			system.shutdown
	}
}


object Main{
		def main(args: Array[String]){
			val system = ActorSystem("PingPongSystem")
			val pong = system.actorOf(Props(new Pong(system)), name = "pong")
			val ping = system.actorOf(Props(new Ping(pong)), name = "ping")
			ping ! StartMessage
		}
}