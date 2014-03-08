package flowexit

object FindMe {
  
  //  https://user:pass@api.flowdock.com/flows/:org/:flow/messages/:message_id
  
  /**
   * username password org flow [until_id]
   */
  def main(args : Array[String]) {
    val auth = AuthInfo(args(0), args(1))
    val flow = FlowIdentifier(args(2), args(3))
    
    var untilId = if (args.length < 5) {
      val emailToIdMap = loadEmailUserMap(auth, flow)
      println(emailToIdMap)
    } else {
      args(4)
    }
  }
  
  def loadEmailUserMap(auth : AuthInfo, flow : FlowIdentifier) : Map[String, String] = {
      val http = Http(auth, flow)
      val json = Json.parseJson(http.listUsers)
      createIdEmailMap(json)
  }
  
  /**
   * Gets an array of these:
   * 
   * {"id":23151,"nick":"Pirkka","name":"Pirkka Jokela","email":"pirkka.jokela@affecto.com",
   * "avatar":"<url>","status":"<text>","disabled":false,"last_activity":1394288261377,"last_ping":1394288261377,
   * "website":null,"in_flow":true}
   */
  def createIdEmailMap(jsonArray : Json) = {
    jsonArray.array.map { json =>
      json.prop("email").str -> json.prop("id").double.toInt.toString
    }.toMap
  }
}