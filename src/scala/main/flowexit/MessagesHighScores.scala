package flowexit

object MessagesHighScores {

  def main(args: Array[String]) {
    val auth = AuthInfo(args(0), args(1))
    val flow = FlowIdentifier(args(2), args(3))
    
    val emailToIdMap = FindMe.loadEmailUserMap(auth, flow)
    val idToEmailMap = emailToIdMap.map(i=>(i._2, i._1))
    
    val mutableMap = new scala.collection.mutable.HashMap[String, Int]().withDefaultValue(0)
    
    val http = Http(auth, flow)
    val untilId = if (args.length < 5) {
      val json = Json.parseJson(http.listMessages)
      ExtractUrls.handleResults(json, highScoresHandler(mutableMap))
    } else {
      Some(args(4))
    }
    
    ExtractUrls.loadAndHandleAll(untilId, http.listMessages, highScoresHandler(mutableMap))
    System.err.println("Prosessed all results.")
    
    mutableMap.foreach{case (u, c) => println(s"$u : $c")}
    
    System.err.println("Printing results now.")
    val resultList = mutableMap.map { case (user, count) => 
      (idToEmailMap.get(user).getOrElse(user), count)
    }.toList.sortWith((a, b)=>a._2 > b._2)
    
    resultList.foreach{case (u, c) => println(s"$u : $c")}
  }
  
  val discardedContent = List("OpenInvitation", "add_people", "uninvite", "invite", "join")
  
  def highScoresHandler(user2count : scala.collection.mutable.Map[String, Int])(js : Json) : Unit = {
    val jsUser = js.prop("user").str
    // The real text content is either in the content property or in the text property of the content object.
    val content = js.prop("content")
    val text = content match {
      case c if c.isProp("type") && c.prop("type").str == "add_rss_feed" => Some(c.prop("description").str)
      case c if c.isProp("type") && discardedContent.contains(c.prop("type").str) => None
      case c if c.isProp("user") => None // New user
      case c if c.isProp("path") => Some("file") // Uploaded file
      case c if c.isStr => Some(c.str)
      case c if c.isProp("text") => Some(c.prop("text").str)
      case c if c.isProp("link") => Some(c.prop("link").str)
    }
    if (text != None) {
      user2count(jsUser) = user2count(jsUser) + 1
      System.err.println(s"Added to user: ${jsUser} for total of ${user2count(jsUser)}." )
    }
  }
}