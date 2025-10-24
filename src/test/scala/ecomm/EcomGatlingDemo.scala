package ecomm

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class EcomGatlingDemo extends Simulation {

  // HTTP protocol configuration
  private val httpProtocol = http
    .baseUrl("https://api-ecomm.gatling.io")
    .headers(Map(
      "authorization" -> "Heff3hKGkEPD4cPGYzwL3tR07MjcUm5cpFDgxTJG1534bZ2yD0u3xkBzJFQzW1Zw",
      "content-type" -> "application/json"
    ))

  private val url = ("https://ecomm.gatling.io")

  // Scenario
  private val ecomm = scenario("E-commerce User Journey")
    .feed(csv("TestData/loginCredentials.csv").circular)
    //    .during(100.seconds) {
    //    .repeat(1) {
    // Home Page
    .exec(
      http("Home Page")
        .get(url + "/")
        .resources(
          http("Session")
            .get("/session")
            .check(jsonPath("$.sessionId").saveAs("c_SessionId")),
          http("List Products 1")
            .get("/products?page=0&search=")
        )
    )
    .pause(3)

    // Login
    .exec(
      http("User Login")
        .post("/login")
        .formParam("username", "#{p_username}")
        .formParam("password", "#{p_password}")
        .check(jsonPath("$.message").is("Login successful"))
        .resources(
          http("List Products 2")
            .get("/products?page=0&search=")
            .check(jsonPath("$.products[*].id").findRandom.saveAs("c_ProductId"))
        )
    )
    .pause(3)

    // Select Product
    .exec(
      http("Select Product")
        .get("/products/#{c_ProductId}")
        .check(bodyString.saveAs("c_ProductDetails"))

    )
    .pause(3)

    // Add to Cart
    .exec(
      http("Add to Cart")
        .post("/cart")
        //.body(RawFileBody("0015_request.json"))
        .body(StringBody(
          """|{
             |  "sessionId": "#{c_SessionId}",
             |  "cart": [
             |    #{c_ProductDetails}
             |  ]
             |}""".stripMargin))
    )
    .pause(3)

    // Checkout
    .exec(
      http("Place Order")
        .post("/checkout")
        //.body(RawFileBody("0017_request.json"))
        .body(StringBody(
          """|{
             |  "sessionId": "#{c_SessionId}",
             |  "cart": [
             |    #{c_ProductDetails}
             |  ]
             |}""".stripMargin))
    )
  //    }


  // Setup
  //  setUp(ecomm.inject(rampUsers(1).during(20))).protocols(httpProtocol).maxDuration(100.second)
  setUp(ecomm.inject(atOnceUsers(1))).protocols(httpProtocol)
}
