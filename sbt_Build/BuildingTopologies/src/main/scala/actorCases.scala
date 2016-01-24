import akka.actor._

case class InitializeList(list : List[ActorRef], index : Int)
case object InitializeActorSystem
case object StartActorSystem
case object ListenToRumour
case object NodeStartNotification
case class StartSpreadingRumour(list : List[ActorRef], index : Int)
case object InitializationComplete
case object StopSystem
case object StopFunction
case object SpreadRumour
case object CreateFrequency
