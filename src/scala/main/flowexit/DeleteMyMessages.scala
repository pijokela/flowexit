package flowexit

/**
 * DELETE /flows/:organization/:flow/messages/:id
 */
object DeleteMyMessages {

  def main(args: Array[String]) {
    val auth = AuthInfo(args(0), args(1))
    val flow = FlowIdentifier(args(2), args(3))
    
    val emailToIdMap = FindMe.loadEmailUserMap(auth, flow)
    val userId = emailToIdMap(auth.userName)
    System.err.println(s"[META] Messages for ${auth.userName} -> $userId")
    
    val http = Http(auth, flow)
    val untilId = if (args.length < 5) {
      val json = Json.parseJson(http.listMessages)
      ExtractUrls.handleResults(json, deleteMineHandler(http, userId))
    } else {
      Some(args(4))
    }
    
    ExtractUrls.loadAndHandleAll(untilId, http.listMessages, deleteMineHandler(http, userId))
  }

  def deleteMineHandler(http : Http, userId : String)(js : Json) : Unit = {
    val jsUser = js.prop("user").str
    if (js.prop("user").str == userId) {
      // The real text content is either in the content property or in the text property of the content object.
      val messageId = js.prop("id").strNumber
      val result = http.deleteMessage(messageId)
      println(s"Deleted message: $messageId. Result code was: $result.")
    }
  }  
}
