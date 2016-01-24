import scala.math._

object main{
  var number = 0;
  var cube = 0;

  def getProperNumberOfNodes(noOfNodes : Int) : Int = {
    var cubeRoot = math.cbrt(noOfNodes)
    var newNo = cubeRoot.toInt
    if((cubeRoot - newNo) != 0){
      val big = math.pow(newNo+1, 3)
      val small = math.pow(newNo, 3)
      if(big-noOfNodes < noOfNodes-small)
        newNo += 1
    }
    println(math.pow(newNo,3))
    number = newNo
    return (math.pow(newNo,3).toInt)
  }

  def createList(len : Int) : List[Char] = {
    var charList = List[Char]()
    for(i <- 0 to len-1){
      charList = ('a'+i).toChar :: charList
    }
    return charList.reverse
  }

  def getNeighbours(index : Int, list : List[Char]) : List[String] = {
    var neighbours = List[String]()
    var square = number*number
    var inn = index % square
    var inn_n = inn % number
    //Right
      if(inn_n + 1 < number)
        neighbours = ("Right :"+list(index+1))::neighbours
    //Left
      if(inn_n - 1 >= 0)
        neighbours = ("Left :"+list(index-1))::neighbours
    //Up
      if(inn - number >= 0)
        neighbours = ("Up :"+list(index-number))::neighbours
    //Down
      if(inn + number < square)
        neighbours = ("Down :"+list(index+number))::neighbours
    //Further
      if(index+square<(cube))
        neighbours = ("Further :"+list(index+(square)))::neighbours
    //Behind
      if(index-square>=0)
        neighbours = ("Behind :"+list(index-(square)))::neighbours

    return neighbours.reverse
  }

  def main(args : Array[String]){
    var len = args(0).toInt
    var list = List[Char]()
    var num = args(1).toInt
    len = getProperNumberOfNodes(len)
    cube = len
    list = createList(len)
    //list.foreach(println)
    println("getting neighbours for "+num)
    getNeighbours(num, list).foreach(println)
  }

}
