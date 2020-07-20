import com.hypercubetools.ktor.moshi.MoshiSerializer
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.*
import io.ktor.http.ContentType
import kotlinx.coroutines.runBlocking

const val FILTER_COLOR = "red"
val ADDED_RATING = Rating(FILTER_COLOR, 1.3)

internal fun runClient(check: Boolean, serverHost: String, serverPort: Int): Int {
    val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = MoshiSerializer()
        }
        defaultRequest {
            host = serverHost
            port = serverPort
        }
    }

    println("Initial ratings")
    var result = runBlocking {
        client.get<RatingResponse>(path = ROUTE)
    }
    if (check) {
        checkResult(result, INITIAL_RATINGS)?.let { return it }
    }
    result.display()

    println("Add rating")
    result = runBlocking {
        client.post<RatingResponse>(path = ROUTE, body = ADDED_RATING) {
            header("Content-Type", ContentType.Application.Json.toString())
        }
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

data class ClientSettings(val check: Boolean, val host: String)