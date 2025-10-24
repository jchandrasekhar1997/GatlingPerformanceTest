package Reqres

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ReqresDemoAPI extends Simulation {

  //Protocol
  val httpProtocol = http
    .baseUrl("https://reqres.in")

  //Scenario
  val scn = scenario("ReqresDemoAPI")
    .exec(http("List Users")
      .get("/api/users?page=2")
      .check(status.is(200),
        jsonPath("$.data[1].first_name").is("Lindsay"))
      //      ,
      //      http("Create User")
      //        .post("/api/users")
      //        .header("content-type", "application/json")
      //        .body(StringBody(
      //          """|{
      //             |    "name": "morpheus",
      //             |    "job": "leader"
      //             |}""".stripMargin)).asJson
      //        .check(status.is(200)'
      //        )
  )

  //SetUp
  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
}
