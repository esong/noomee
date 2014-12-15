package controllers

import play.api.mvc.{Action, Controller}
import yelp.YelpAPI

object RestaurantController extends Controller {

  val yelpAPI = new YelpAPI()

  def search(term: String, location: String) = Action {
    // TODO validate term and location
    Ok(yelpAPI.searchForBusinessesByLocation(term, location))
  }
}
