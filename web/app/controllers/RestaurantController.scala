package controllers

import noomee.Randomizer
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.{Action, Controller}
import yelp.YelpAPI

object RestaurantController extends Controller {

  val yelpAPI = new YelpAPI()
  val randomizer = new Randomizer()

  def search(term: String, lati: Double, longi: Double) = Action {
    // TODO validate term and location
    val responseJson = yelpAPI.searchForBusinessesByLatLong(term, lati, longi)
    val response = Json.parse(responseJson)
    Ok(Json.prettyPrint(response))
  }

  def random(term: String, lati: Double, longi: Double, tags: List[String]) = Action {
    val responseJson = yelpAPI.searchForBusinessesByLatLong(term, lati, longi)
    Ok(Json.prettyPrint(randomizer.random(Json.parse(responseJson), tags)))
  }

  def categories(term: String, lati: Double, longi: Double) = Action {
    val responseJson = yelpAPI.searchForBusinessesByLatLong(term, lati, longi)
    /* Yelp's format
     * businesses : [categories : [["Chinese", chinese]] ]
     * We want to count the number of each categories
     */
    val categoryMap = (for {
      e <- (Json.parse(responseJson) \ "businesses").as[JsArray].value
      e1 <- (e \ "categories").as[JsArray].value
    } yield e1.as[JsArray].value)
      .groupBy(l => l).map(t =>
      Json.toJson(Map(
        "name" -> t._1(0),
        "query" -> t._1(1),
        "count" -> Json.toJson(t._2.length)
      )))

    Ok(Json.toJson(categoryMap))
  }
}
