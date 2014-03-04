package flowexit

import java.util.Date

object App {
  
  /**
   * username password org flow [until_id]
   */
  def main(args : Array[String]) {
    val http = Http(args(0), args(1), args(2), args(3))
    var untilId = if (args.length < 5) {
      val json = Json.parseJson(http.listMessages)
      handleResults(json)
    } else {
      args(4)
    }
    
    var i = 0
    while (i < 10000) {
      i = i + 1
      val json = Json.parseJson(http.listMessages(untilId))
      untilId = handleResults(json)
    }
  }
  
  def handleResults(json : Json) : String = {
    val firstObject = json.array.head 
    val sent = if (firstObject.isProp("sent")) Some(firstObject.prop("sent").double) else None
    sent.map { s =>
      System.err.println("[META] Sent:" + new Date(s.toLong))
    }
    val firstId = firstObject.prop("id").str
    
    val discardedContent = List("OpenInvitation", "add_people", "uninvite", "invite", "join")
    
    json.array.map { js=>
      // The real text content is either in the content property or in the text property of the content object.
      val content = js.prop("content")
      val text = content match {
        case c if c.isProp("type") && c.prop("type").str == "add_rss_feed" => c.prop("description").str
        case c if c.isProp("type") && discardedContent.contains(c.prop("type").str) => ""
        case c if c.isProp("user") => "" // New user
        case c if c.isProp("path") => "" // Uploaded file
        case c if c.isStr => c.str
        case c if c.isProp("text") => c.prop("text").str
        case c if c.isProp("link") => c.prop("link").str
      }
      findUrls(text).map(println(_))
    }
    firstId
  }
  
  val urlRegex = "(ht|f)tp(s?)\\:\\/\\/[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(\\/?)([a-zA-Z0-9\\-‌​\\.\\?\\,\\'\\/\\\\\\+&amp;%\\$#_]*)?".r
  private def findUrls(text : String) : List[String] = {
    urlRegex.findAllMatchIn(text).map(m=>m.matched).toList
  }
  
  private def encode(string : String) = 
    java.net.URLEncoder.encode(string, "UTF-8")
}
