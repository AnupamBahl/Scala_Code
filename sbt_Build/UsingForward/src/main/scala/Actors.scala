import akka.actor._

case object Start
case object Receive
case object Increment
case object Found

class ActorOne(actorTwo : ActorRef) extends Actor{
  var hops = 0
  var check = true

  def receive = {
    case Start =>
      println("Start of one")
      if(check)
        actorTwo ! Receive
      else
        sender ! Found

    case Receive =>
      println("Receive of One")
      sender ! Increment
      //someone closest found
      if(check)
        actorTwo forward Receive
      else
        sender ! Found

    case Found =>
      print("Number of hops : ")
      println(hops)

    case Increment =>
      hops += 1
      println("Increment of one")

  }
}

class ActorTwo(actorThree : ActorRef) extends Actor{
  var hops = 0
  var check = true

  def receive = {
    case Start =>
      println("Start of Two")
      if(check)
        actorThree ! Receive
      else
        sender ! Found

    case Receive =>
      println("Receive of Two")
      sender ! Increment
      //someone closest found
      if(check)
        actorThree forward Receive
      else
        sender ! Found

    case Found =>
      print("Number of hops : ")
      println(hops)

    case Increment =>
      hops += 1
      println("Increment of Two")
    }
}

class ActorThree extends Actor{
  var hops = 0
  var check = false

  def receive = {
    case Start =>
      println("Start of Three")
      if(check)
        self ! Receive
      else
        sender ! Found

    case Receive =>
      println("Receive of Three")
      sender ! Increment
      //someone closest found
      if(check)
        self forward Receive
      else
        sender ! Found

    case Found =>
      print("Number of hops : ")
      println(hops)

    case Increment =>
      hops += 1
      println("Increment of Three")

  }
}
