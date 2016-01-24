import scala.util.control.Breaks._


object StringGenerator{
	val charList : Array[Char] = new Array[Char](100)
	charList(0) = ' '
	var charListLoc = 0
	val baseString = "AnupamBahl@"
	var found = false
	
	def getList(length : Int): Array[String] = {
		val arrLst : Array[String] = new Array[String](length)
		for(i <- 0 to length-1)
			arrLst(i) = generateNextString()
		return arrLst
	}
	
	def generateNextString():String = {
		if(charList(charListLoc) == 126){
			breakable {
				for(i <- (0 to charListLoc-1).reverse){
					if(charList(i)<126){
						charList(i) = (charList(i) + 1).toChar
						for(j <- i+1 to charListLoc)
							charList(j) = '!'
						found = true
						break
					}
				}
			}
			if(!found){
				if(charListLoc == 99)
					println("have to stop now")
				else{
					charListLoc += 1
					for(i <- (0 to charListLoc))
						charList(i) = '!'
				}
			}
			else{
				found = false
			}		
		} 
		else
			charList(charListLoc) = (charList(charListLoc) + 1).toChar
	
		return ( baseString + charList.slice(0, charListLoc+1).mkString ) 
	}

	def setStart(startPoint : Int) ={
		getList(startPoint)
	}
	def main(args : Array[String]){
		var count = 3000000
		//while(count > 0){
		//	getList(5000)
		//	count = count - 5000
		//}
		setStart(93)
		println(generateNextString())
	}
}
