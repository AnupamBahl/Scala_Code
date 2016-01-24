case object StartOperation
case object GetErrors
case object ReturnErrors
case object AddFriend
case object AcceptAllRequests
case object GetFriendsList
case object UserCreated
case object AddFriends
case object FriendsAdded
case object AcceptedRequests
case object StartPostingOne
case object StartPostingTwo

case class AddFriend(friendId : Int)
case class AcceptRequest(friendId : Int)
case class InitializeUser(name : String, userName : String)
case class ReceiveErrorsCount(num : Int)
case class GetAnotherUserProfile(userId : Int)
case class PostOnUserPage(userId : Int, postData : String)
case class GetUserWallPage(userId : Int)
