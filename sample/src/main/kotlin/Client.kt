import com.hypercubetools.ktor.moshi.moshi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.http.ContentType
import kotlinx.coroutines.runBlocking

const val FILTER_COLOR = "red"
val ADDED_RATING = Rating(FILTER_COLOR, 1.3)

internal fun runClient(check: Boolean, serverHost: String, serverPort: Int): Int {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            moshi()
        }
        defaultRequest {
            host = serverHost
            port = serverPort
        }
    }

    println("Initial ratings")
    var result = runBlocking {
        client.get(ROUTE).body<RatingResponse>()
    }
    if (check) {
        checkResult(result, INITIAL_RATINGS)?.let { return it }
    }
    result.display()

    println("Add rating")
    result = runBlocking {
        client.post(ROUTE) {
            header("Content-Type", ContentType.Application.Json.toString())
            setBody(ADDED_RATING)
        }.body()
    }
    if (check) {
        val expectedAddResponseRatings = INITIAL_RATINGS
                .asSequence().filter { it.color == FILTER_COLOR }
                .plus(ADDED_RATING)
                .toList()
        checkResult(result, expectedAddResponseRatings)?.let { return it }
    }
    result.display()

    return ExitCodes.SUCCESS
}

fun checkResult(toCheck: RatingResponse, expected: List<Rating>): Int? {
    if (toCheck.ratings != expected) {
        println("Bad ratings: ${toCheck.ratings.joinToString(",")}")
        return ExitCodes.BAD_SERVER_RESPONSE
    }
    if (toCheck.average != expected.averageRating()) {
        println("Bad average: ${toCheck.average}")
        return ExitCodes.BAD_SERVER_RESPONSE
    }
    return null
}
