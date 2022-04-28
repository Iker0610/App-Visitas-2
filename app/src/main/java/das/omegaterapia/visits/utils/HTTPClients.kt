package das.omegaterapia.visits.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import das.omegaterapia.visits.model.entities.AuthUser
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton


/*******************************************************************************
 ****                               Exceptions                              ****
 *******************************************************************************/

class AuthenticationException : Exception()
class UserExistsException : Exception()


/*******************************************************************************
 ****                         Response Data Classes                         ****
 *******************************************************************************/

/**
 * Data class that represents server response when an [accessToken] is request.
 */
@Serializable
data class TokenInfo(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("token_type") val tokenType: String,
)


/*******************************************************************************
 ****                          Bearer Token Storage                         ****
 *******************************************************************************/

/**
 * [MutableList] to save retrieves [BearerTokens]
 */
private val bearerTokenStorage = mutableListOf<BearerTokens>()


/*******************************************************************************
 ****                              HTTP Clients                             ****
 *******************************************************************************/

/**
 * HTTP Client that makes petitions to the API to authenticate, retrieve access token and create users.
 */
@Singleton
class AuthenticationClient @Inject constructor() {


    /*************************************************
     **         Initialization and Installs         **
     *************************************************/

    private val httpClient = HttpClient(CIO) {

        // If return code is not a 2xx then throw an exception
        expectSuccess = true

        // Install JSON handler (allows to receive and send JSON data)
        install(ContentNegotiation) { json() }

        // Handle non 2xx status responses
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                when {
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Unauthorized -> throw AuthenticationException()
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Conflict -> throw UserExistsException()
                    else -> {
                        exception.printStackTrace()
                        throw exception
                    }
                }
            }
        }
    }


    /*************************************************
     **                   Methods                   **
     *************************************************/

    @Throws(AuthenticationException::class, Exception::class)
    suspend fun authenticate(user: AuthUser) {
        val tokenInfo: TokenInfo = httpClient.submitForm(
            url = "https://api.omegaterapia.das.ranap.eus/auth/token",
            formParameters = Parameters.build {
                append("grant_type", "password")
                append("username", user.username)
                append("password", user.password)
            }).body()

        bearerTokenStorage.add(BearerTokens(tokenInfo.accessToken, tokenInfo.refreshToken))
    }

    @Throws(UserExistsException::class)
    suspend fun createUser(user: AuthUser) {
        httpClient.post("https://api.omegaterapia.das.ranap.eus/users") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }
}


/**
 * HTTP Client that makes authenticated petitions to REST API.
 *
 * It manages automatic access token refresh.
 */
@Singleton
class APIClient @Inject constructor() {

    /*************************************************
     **         Initialization and Installs         **
     *************************************************/

    private val httpClient = HttpClient(CIO) {

        // If return code is not a 2xx then throw an exception
        expectSuccess = true

        // Install JSON handler (allows to receive and send JSON data)
        install(ContentNegotiation) { json() }

        // Install Bearer Authentication Handler
        install(Auth) {
            bearer {

                // Define where to get tokens from
                loadTokens { bearerTokenStorage.last() }

                // Send always the token, do not  wait for a 401 before adding the token to the header
                sendWithoutRequest { request -> request.url.host == "api.omegaterapia.das.ranap.eus" }

                // Define token refreshing flow
                refreshTokens {

                    // Get the new token
                    val refreshTokenInfo: TokenInfo = client.submitForm(
                        url = "https://api.omegaterapia.das.ranap.eus/auth/refresh",
                        formParameters = Parameters.build {
                            append("grant_type", "refresh_token")
                            append("refresh_token", oldTokens?.refreshToken ?: "")
                        }
                    ) { markAsRefreshTokenRequest() }.body()

                    // Add tokens to Token Storage and return the newest one
                    bearerTokenStorage.add(BearerTokens(refreshTokenInfo.accessToken, oldTokens?.refreshToken!!))
                    bearerTokenStorage.last()
                }
            }
        }
    }


    /*************************************************
     **                   Methods                   **
     *************************************************/

    //--------   User subscription to FCM   --------//

    suspend fun subscribeUser(FCMClientToken: String) {
        httpClient.post("https://api.omegaterapia.das.ranap.eus/notifications/subscribe") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("fcm_client_token" to FCMClientToken))
        }
    }


    //----------   User's profile image   ----------//

    suspend fun getUserProfile(): Bitmap {
        val response = httpClient.get("https://api.omegaterapia.das.ranap.eus/profile/image")
        val image: ByteArray = response.body()
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    suspend fun uploadUserProfile(image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        httpClient.submitFormWithBinaryData(
            url = "https://api.omegaterapia.das.ranap.eus/profile/image",
            formData = formData {
                append("file", byteArray, Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=profile_image.png")
                })
            }
        ) { method = HttpMethod.Put }
    }
}
