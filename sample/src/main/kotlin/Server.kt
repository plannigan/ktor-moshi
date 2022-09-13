import com.hypercubetools.ktor.moshi.moshi
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

private val ratings = INITIAL_RATINGS.toMutableList()

fun ratingResponse(color: String?): RatingResponse {
    val filtered = ratings.filter { color == null || it.color == color }
    val average = filtered.averageRating()
    return RatingResponse(average, filtered)
}

fun runServer(port : Int) : Int {
    val server = embeddedServer(Netty, port) {
        install(CallLogging)

        install(ContentNegotiation) {
            moshi()
        }

        routing {
            // Test with the browser at http://localhost:8080/ratings?color=blue
            get(ROUTE) {
                val color = call.parameters["color"]
                call.respond(ratingResponse(color))
            }

            // Test with `curl -X POST -H'Content-Type: application/json' -d '{"color": "red", "rating": 3.7}' http://localhost:8080/ratings`
            post(ROUTE) {
                val rating = call.receive<Rating>()
                ratings.add(rating)
                call.respond(ratingResponse(rating.color))
            }
        }
    }
    server.start(wait = true)
    return 0
}
