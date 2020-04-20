/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.concurrent.TimeUnit

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class SimulationTest extends Simulation {

    val apiAuthenticateScenario = scenario("注册中心-认证接口").repeat(1000, "n") {
        exec(
            http("api-authenticate-post")
                .post("http://localhost:8761/api/authenticate")
                .header("Content-Type", "application/json")
                .body(StringBody("""{"username":"admin","password":"21232f297a57a5a743894a0e4a801fc3"}"""))
                .check(status.is(200))
        ).pause(Duration.apply(5, TimeUnit.MILLISECONDS))
    }
    setUp(apiAuthenticateScenario.inject(atOnceUsers(5000))).maxDuration(FiniteDuration.apply(1, "minutes"))
}
