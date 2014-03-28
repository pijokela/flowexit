package flowexit

import scala.util.parsing.json._

object Json {
  def parseJson(str : String) = 
    Json(JSON.parseRaw(str).get)
}

case class Json(raw : Any) {
  def array = raw.asInstanceOf[JSONArray].list.map(Json(_))
  
  def prop(name : String) : Json = {
    val jsObj = raw.asInstanceOf[JSONObject]
    val jsMap = jsObj.obj
    Json(jsMap(name))
  }
  
  def str = raw.toString
  def strNumber = {
    val string = str
    string.split("\\.").head
  }
  def double = raw.asInstanceOf[Double]
  
  def isProp(name : String) = isObj && raw.asInstanceOf[JSONObject].obj.contains(name)
  def isObj = raw.isInstanceOf[JSONObject]
  def isStr = raw.isInstanceOf[String]
  def isArray = raw.isInstanceOf[JSONArray]
}