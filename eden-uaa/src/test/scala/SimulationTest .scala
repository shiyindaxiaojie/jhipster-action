import java.util.concurrent.TimeUnit

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.language.postfixOps

class SimulationTest extends Simulation {

    val scn = scenario("api-users").repeat(1000, "n") {
        exec(
            http("api-users-post")
                .post("http://localhost:9999/api/users")
                .header("Content-Type", "application/json")
                .body(StringBody("""{"login":"guoyuanlu${n}","password":"123456${n}","email":"1813986321@qq.com"}"""))
                .check(status.is(200))
        ).pause(Duration.apply(5, TimeUnit.MILLISECONDS))
    }.repeat(1000, "n") {
        exec(
            http("api-users-get")
                .get("http://localhost:9999/api/users")
                .check(status.is(200))
        )
    }
    setUp(scn.inject(atOnceUsers(30))).maxDuration(FiniteDuration.apply(10, "minutes"))

}
