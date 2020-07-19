import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RatingResponse(val average: Double, val ratings: List<Rating>) {
    fun display() {
        println("Ratings:")
        ratings.forEach {
            println(it)
        }
        println("Average: $average")
    }
}

@JsonClass(generateAdapter = true)
data class Rating(val color: String, val rating: Double)

const val HOST = "localhost"
const val PORT = 8899
const val ROUTE = "/ratings"

val INITIAL_RATINGS = listOf(
        Rating("blue", 4.3),
        Rating("red", 4.1),
        Rating("blue", 4.8),
        Rating("green", 3.2),
        Rating("green", 2.0),
        Rating("blue", 5.0),
        Rating("green", 3.8),
        Rating("red", 1.5),
        Rating("blue", 4.4)
)

object ExitCodes {
    const val SUCCESS = 0
    const val BAD_ARG = 1
    const val BAD_SERVER_RESPONSE = 2
}

fun List<Rating>.averageRating() = map { it.rating }.average()
