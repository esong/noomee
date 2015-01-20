package noomee

import play.api.Logger
import play.api.libs.json.{JsArray, JsValue}

import scala.util.Random

/**
 * Created by esong on 2015-01-11.
 */
class Randomizer {
  val logger = Logger.logger
  val randomGenerator = Random

  def random(json: JsValue, tags: Seq[String]): JsValue = {
    val total = (json \ "total").as[Int];
    val biz : Seq[JsValue] = (json \ "businesses").as[Seq[JsValue]]
    val res = biz.filter(value => tags.isEmpty ||
      (value \ "categories").as[Seq[JsArray]].map(i => tags.contains(i.value.seq(1).as[String])).contains(true))

    res(randomGenerator.nextInt(res.size))
  }
}