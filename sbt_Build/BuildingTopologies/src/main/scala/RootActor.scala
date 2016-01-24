import akka.actor._

class DeathWatchActor(noOfNodes : Int) extends Actor{
  var localSum = 0

  def receive = {
    case NodeStartNotification =>
      localSum += 1
      //println("Actors active : "+localSum.toString)
      if(localSum == noOfNodes)
        context.stop(self)
  }
}


class RootActor(system : ActorSystem, noOfNodes : Int, topology : String, algorithm : String) extends Actor{
  val fullTop = "full"
  val d3Top = "3D"
  val lineTop = "line"
  val imp3DTop = "imp3D"
  val gossipAlgo = "gossip"
  val pushSumAlgo = "push-sum"

  var initializationComplete = false
  var initializationCount = 0
  var time = System.currentTimeMillis
  var listOfAllActors = List[ActorRef]()
  val deathWatchActor = context.actorOf(Props(new DeathWatchActor(noOfNodes)), "DeathWatchActor")

  //Watch the child to know when everyone has received the data
  context.watch(deathWatchActor)

  def receive = {

    case InitializeActorSystem => {
      if(!initializationComplete)
      {
        //Create a list of the number of actors required
        if(!initializeAllActorsList()){
          printf("Invliad Algorithm. Valid values are : '%s' and '%s'\n", gossipAlgo, pushSumAlgo)
          sender ! "error"
        }
        //Initiate actors with respective neighbours based on topology
        if(!createTopology()){
          printf("Invalid Topology. Valid values are : '%s', '%s', '%s' and '%s'\n",fullTop, d3Top, lineTop, imp3DTop)
          sender ! "error"
        }
        initializationComplete = true
      }
      if(initializationCount == noOfNodes)
        sender ! "true"
      else
        sender ! "false"
    }

    case StartActorSystem => {
      val randomObject = scala.util.Random
      //Start measuring time
      time = System.currentTimeMillis
      //Start an actor in network topology randomObject.nextInt(noOfNodes-1)
      listOfAllActors(0) ! ListenToRumour
    }

    case InitializationComplete =>
      initializationCount += 1


    case Terminated(deatRef) => {
      //Stop all processing and print time
      time = System.currentTimeMillis - time
      printf("\n\n_________________________________\n")
      println("System convergence time : "+time)
      printf("_________________________________\n\n")
  
      system.shutdown
    }
  }

  // Creating a list of all actors based on their algorithm
  def initializeAllActorsList() : Boolean = {
    if(algorithm == gossipAlgo){
      for(i<- 0 to noOfNodes-1){
        val actor = context.actorOf(Props(new GossipAlgorithmActor(deathWatchActor)), "GossipAlgorithmActor"+i.toString)
        listOfAllActors = listOfAllActors ::: List[ActorRef](actor)
      }
    return true
    }
    else if(algorithm == pushSumAlgo){
      for(i <- 0 to noOfNodes-1){
        val actor = context.actorOf(Props[PushSumAlgorithmActor], "PushSumAlgorithmActor"+i.toString)
        listOfAllActors = listOfAllActors ::: List[ActorRef](actor)
      }
    return true
    }
    return false
  }

  //Initializing all actors with their respective lists
  def createTopology() : Boolean = {
    topology match{
        case `fullTop` =>
          //Initialize first and last actors of list
          listOfAllActors(0) ! InitializeList(listOfAllActors.drop(1), 0)
          listOfAllActors(noOfNodes-1) ! InitializeList(listOfAllActors.dropRight(1), noOfNodes-1)
          //Initialize the rest
          for(i <- 1 to noOfNodes-2){
            var tempList = listOfAllActors.dropRight(noOfNodes-i) ::: listOfAllActors.drop(i+1)
            listOfAllActors(i) ! InitializeList(tempList , i)
          }
          return true

        case `d3Top` =>
          return false

        case `lineTop` =>
          //Initialize first and last actors of the list
          listOfAllActors(0) ! InitializeList(List(listOfAllActors(1)), 0)
          listOfAllActors(noOfNodes-1) ! InitializeList(List(listOfAllActors(noOfNodes-2)), noOfNodes-1)
          //Initialize the rest
          for(i <- 1 to noOfNodes-2){
            listOfAllActors(i) ! InitializeList(List(listOfAllActors(i-1)) ::: List(listOfAllActors(i+1)), i)
          }
          return true

        case `imp3DTop` =>
          return false

        case _ =>
          return false
    }
  }


}
