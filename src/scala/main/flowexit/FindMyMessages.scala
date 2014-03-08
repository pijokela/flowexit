package flowexit

object FindMyMessages {

  def main(args: Array[String]) {
    val auth = AuthInfo(args(0), args(1))
    val flow = FlowIdentifier(args(2), args(3))
    
    val emailToIdMap = FindMe.loadEmailUserMap(auth, flow)
    val userId = emailToIdMap("eeva-liisa.lennon@affecto.com")
    System.err.println(s"[META] Messages for ${auth.userName} -> $userId")
    
    val http = Http(auth, flow)
    val untilId = if (args.length < 5) {
      val json = Json.parseJson(http.listMessages)
      ExtractUrls.handleResults(json, printMineHandler(userId))
    } else {
      Some(args(4))
    }
    
    ExtractUrls.loadAndHandleAll(untilId, http.listMessages, printMineHandler(userId))
  }
  
  val discardedContent = List("OpenInvitation", "add_people", "uninvite", "invite", "join")
  
  def printMineHandler(userId : String)(js : Json) : Unit = {
    val jsUser = js.prop("user").str
    if (js.prop("user").str == userId) {
      // The real text content is either in the content property or in the text property of the content object.
      val content = js.prop("content")
      val text = content match {
        case c if c.isProp("type") && c.prop("type").str == "add_rss_feed" => Some(c.prop("description").str)
        case c if c.isProp("type") && discardedContent.contains(c.prop("type").str) => None
        case c if c.isProp("user") => None // New user
        case c if c.isProp("path") => None // Uploaded file
        case c if c.isStr => Some(c.str)
        case c if c.isProp("text") => Some(c.prop("text").str)
        case c if c.isProp("link") => Some(c.prop("link").str)
      }
      text.map(println _)
    }
  }
}