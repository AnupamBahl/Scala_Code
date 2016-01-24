import akka.actor._

object Main {

  var noOfClients = 10

  def main(args : Array[String]){
    val system = ActorSystem("ClientSystem")

    if(args.length < 1){
      printf("Using default number of clients : %d\n", noOfClients)
    }
    else{
      noOfClients = args(0).toInt
      printf("Using %d number of clients\n", noOfClients)
    }

    val topologyActor = system.actorOf(Props(new TopologyActor(system, noOfClients)), "TopologyActor")
    topologyActor ! StartOperation
  }
}

class TopologyActor(system : ActorSystem, noOfClients : Int) extends Actor{
  var actorsList = List[ActorRef]()
  var totalErrors = 0
  var usersStarted = 0
  var friendsAdded = 0
  var inYet = false
  var friendlistmap = scala.collection.mutable.Map[Int, List[Int]]()
  var two = false;

  def receive = {
    case StartOperation => {
      for(i <- 0 to noOfClients){
          val id = i
          val strId = id.toString()
          val actor = context.actorOf(Props(new ClientActor(id, system)), strId)
          actor ! InitializeUser("Actor "+strId, "actor"+strId);
          actorsList = actorsList ::: List(actor)
      }
      Thread sleep 2000
      println("After Creating ...... ")
      actorsList(4) ! AddFriend(8)
      actorsList(4) ! AddFriend(5)
      actorsList(4) ! AddFriend(6)
      Thread sleep 1000
      println("Accepting all requests .......")
      actorsList.foreach((actor : ActorRef) => actor ! AcceptAllRequests)
      Thread sleep 2000
      println("Posting Data on friend 6.......")
      actorsList(4) ! PostOnUserPage(6, "The first post ever")
      Thread sleep 1000
      println("Getting user profile of friend 5 .......")
      actorsList(4) ! GetAnotherUserProfile(5)
      Thread sleep 1000
      println("Getting wall posts of friend 6 .........")
      actorsList(4) ! GetUserWallPage(6)
      Thread sleep 1000
      actorsList(4) ! GetFriendsList
      Thread sleep 1000
      system.shutdown()
    }

    case UserCreated =>{
      if(usersStarted == noOfClients && !inYet){
        inYet = true
        self ! AddFriends
      }
      else if(usersStarted < noOfClients){
        usersStarted += 1
      }
    }

    case AddFriends => {
      var first = (noOfClients/2).toInt
      var count = 0
      for(i <- 0 to first ){
        count += 100
        //30 100 150 500
        var tempList = getListOfNumbers(100)
        friendlistmap += (i -> tempList)
        tempList.foreach((num : Int) => actorsList(i) ! AddFriend(num))
      }
      for( i <- first+1 to noOfClients-1){
        count += 300
        var tempList = getListOfNumbers(300)
        friendlistmap += (i -> tempList)
        tempList.foreach((num : Int) => actorsList(i) ! AddFriend(num))
      }

      usersStarted = count
      //printf("No of users processed for friends : %d\n", usersStarted)
    }

    case FriendsAdded => {
      friendsAdded += 1
      if(friendsAdded == usersStarted){
        printf("Friends requests sent :%d out of :%d\n", friendsAdded, usersStarted)
        friendsAdded = 0
        actorsList.foreach((actor : ActorRef) => actor ! AcceptAllRequests)
      }
    }

    case AcceptedRequests => {
      if(friendsAdded == noOfClients){
        printf("Friend Requests Accepted by :%d users\n",friendsAdded)
        println("\n\n##########\nSystem ready to start simulation\n##########\n")
        Thread sleep 2000
        println("\n")
        self ! StartPostingOne
      }
      else if(friendsAdded < noOfClients){
      friendsAdded += 1
      //
      }
    }

    case StartPostingOne => {
      printf("Group one started posting data to random friends, covering each friend exactly once.\nTotal 5000 requests.\n")
      for(i <-1 to 500){
        var str = "From Actor :"+i.toString()
        friendlistmap(i).foreach((num : Int) => actorsList(i) ! PostOnUserPage(num, str))
      }
      Thread sleep 30000
      usersStarted = 0
      for(i <- 1 to 500){
        actorsList(i) ! ReturnErrors
      }
    }

    case StartPostingTwo => {
      two = true
      printf("Group Two started posting data to random friends, covering each friend exactly once.\nTotal 15000 requests.\n")
      Thread sleep 5000
      for(i <-500 to 999){
        var str = "From Actor :"+i.toString()
        friendlistmap(i).foreach((num : Int) => actorsList(i) ! PostOnUserPage(num, str))
      }
      Thread sleep 30000
      usersStarted = 0
      for(i <- 500 to 999){
        actorsList(i) ! ReturnErrors
      }
    }

    case GetErrors => {
      for(i <- 1 to actorsList.length){
        actorsList(i) ! ReturnErrors
      }
    }

    case ReceiveErrorsCount(num : Int) => {
      if(two){
        totalErrors += num
        usersStarted += 1
        if(usersStarted == 500){
          printf("Number of conflict errors for this group : %d, with timeout 1 sec\n", totalErrors)
          system.shutdown()
        }
      }
      else{
      totalErrors += num
      usersStarted += 1
      if(usersStarted == 500){
        printf("Number of conflict errors for this group : %d, with timeout 1 sec\n\n", totalErrors)
        self ! StartPostingTwo
      }
      }
    }
  }

  def getListOfNumbers(num : Int) : List[Int]={
    val r = scala.util.Random
    var list = List[Int]()
    for( i<- 1 to num){
      list = list ::: List(r.nextInt(noOfClients-1))
    }
    return list
  }
}
