import com.hypercubetools.ktor.moshi.moshi
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

private val ratings = INITIAL_RATINGS.toMutableList()

fun ratingResponse(color: String?): RatingResponse {
    val filtered = ratings.filter { color == null || it.color == color }
    val average = filtered.averageRating()
    return RatingResponse(average, filtered)
}

fun runServer() : Int {
    val server = embeddedServer(Netty, PORT) {
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
