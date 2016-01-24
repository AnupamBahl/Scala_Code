import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

object ObjectMain {
  var noOfNodes = 10
  var topology = "full"
  var algorithm = "gossip"
  var result = "false"

  def main(args : Array[String]){

      // Taking in arguments
      if(args.length < 3){
        printf("\n\n.................................\n")
        println("Not enough arguments. Format : projectName.scala numberOfNodes topology algorithm")
        printf("Using default arguments : numberOfNodes = %d, topology : %s, algoritm : %s\n", noOfNodes, topology, algorithm)
      }
      else{
        noOfNodes = args(0).toInt
        topology = args(1)
        algorithm = args(2)
      }

      //Creating a basic actor system
      val topologySystem = ActorSystem("topologySystem")

      //Create Main Actor
      val rootActor = topologySystem.actorOf(Props(new RootActor(topologySystem, noOfNodes, topology, algorithm)), "RootActor")

      //Waiting for the topology to get created
      while(result == "false"){
        implicit val timeout = Timeout(5 seconds)
        val future = rootActor ? InitializeActorSystem
        result = Await.result(future, timeout.duration).asInstanceOf[String]
      }

      //Problems in topology creation
      if(result == "error"){
        topologySystem.shutdown()
        return
      }

      //Start Protocol
      println("Topology Created")
      rootActor ! StartActorSystem
      //printf(".................................\n\n\n")
      //Thread sleep 5000
      //topologySystem.shutdown()

  }

}
