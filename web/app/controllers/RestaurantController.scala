package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import yelp.YelpAPI

object RestaurantController extends Controller {

  val yelpAPI = new YelpAPI()

  def search(term: String, lati: Double, longi: Double) = Action {
    // TODO validate term and location
    val responseJson = yelpAPI.searchForBusinessesByLatLong(term, lati, longi)
    val response = Json.parse(responseJson)
    Ok(response)
  }
}
