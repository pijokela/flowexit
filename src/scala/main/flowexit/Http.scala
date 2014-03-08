package flowexit

import java.net._
import scala.io._

/**
 * https://user:pass@api.flowdock.com/flows/:org/:flow/messages/:message_id
 */
case class Http(auth : AuthInfo, flow : FlowIdentifier) {
  val org = flow.org
  val flowName = flow.flowName
  val user = auth.userName
  val password = auth.password
  
  def get(requestUrl : String) : String = {
    val url = new URL(requestUrl)
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestProperty("Authorization", authorizationHeader);
    if (connection.getResponseCode() != 200)
      throw new IllegalStateException("Response code: " + connection.getResponseCode() + " from url: " + url);
    val source = Source.fromInputStream(connection.getInputStream())("UTF-8")
    source.getLines.mkString("\n")
  }
  
  /**
   * https://user:pass@api.flowdock.com/flows/:org/:flow/messages/:message_id
   * GET /flows/:organization/:flow/messages
   */
  def listMessages() = {
    val url = s"https://api.flowdock.com/flows/$org/$flowName/messages"
    System.err.println("[META] GET: " + url)
    get(url)
  }
  
  def listMessages(untilId : String) = {
    val url = s"https://api.flowdock.com/flows/$org/$flowName/messages?until_id=$untilId"
    System.err.println("[META] GET: " + url)
    get(url)
  }
  
  def listUsers() = {
    val url = s"https://api.flowdock.com/flows/$org/$flowName/users"
    System.err.println("[META] GET: " + url)
    get(url)
  }
  
  def post(requestUrl : String, params : Map[String, String]) : String = {
    val url = new URL(requestUrl)
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    val source = Source.fromInputStream(connection.getInputStream())("UTF-8")
    if (connection.getResponseCode() != 200) {
      throw new IllegalStateException("Response code: " + connection.getResponseCode() + " from url: " + url + "\n" + source.getLines.mkString("\n"));
    }
    source.getLines.mkString("\n")
  }
  
  /*
   * conn.setRequestProperty("Authorization", basicAuth);
   */
  def authorizationHeader() = {
    val userpass = user + ":" + password
    val basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes())
    basicAuth
  }
} 