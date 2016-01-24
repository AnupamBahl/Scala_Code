import akka.actor._

class GossipChildActor extends Actor{
  val randomObject = scala.util.Random
  var randomInt = 0
  var limit = 0
  var selflist = List[ActorRef]()
  var selfindex = 0

  def receive = {
    case StartSpreadingRumour(list : List[ActorRef], index : Int) =>
      limit = list.length
      selflist = list
      selfindex = index
    //  println(list.length+" "+limit.toString+" Index : "+index.toString)
      self ! SpreadRumour

    case SpreadRumour =>
      randomInt = randomObject.nextInt(limit)
      if(randomInt != selfindex)
        selflist(randomInt) ! ListenToRumour
      //println("here for actor : "+selfindex.toString)
      self ! CreateFrequency

    case CreateFrequency =>
      Thread sleep 200
      self ! SpreadRumour
  }
}

class GossipAlgorithmActor(deathWatchActor : ActorRef) extends Actor{
  var personalList = List[ActorRef]()
  var timeToLive = 10
  var index = 0
  var startedSpreading = false
  val gossipChildActor = context.actorOf(Props[GossipChildActor], "GossipChildActor"+index.toString)

  def receive = {
    case InitializeList(list : List[ActorRef], idx : Int)=> {
      personalList = list
      index = idx
    //  printf("Index : %d, List Length : %d\n",index, personalList.length)
      sender ! InitializationComplete
    }

    case ListenToRumour => {
      if(!startedSpreading){
        startedSpreading = true
        gossipChildActor ! StartSpreadingRumour(personalList, index)
        deathWatchActor ! NodeStartNotification
      }
      if(timeToLive < 1){
      //  println("inside kill zone")
        context.stop(gossipChildActor)
      }
      timeToLive -= 1
    }

    case StopFunction =>
    //  println("###############################################")
      context.stop(gossipChildActor)
      context.stop(self)
  }

}



class PushSumAlgorithmActor extends Actor{
  var personalList = List[ActorRef]()
  var index = 0
  val r = scala.util.Random

  def receive = {
    case InitializeList(list : List[ActorRef], idx : Int)=>
      personalList = list
      index = idx
  }
}
