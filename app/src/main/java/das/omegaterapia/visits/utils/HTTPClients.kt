package das.omegaterapia.visits.utils

import android.graphics.Bitmap
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


class AuthenticationException : Exception()
class UserExistsException : Exception()


/**
 * Data class que representa el JSON devuelto por la API al solicitar un [accessToken].
 */
@Serializable
data class TokenInfo(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("token_type") val tokenType: String,
)


/**
 * Lista para almacenar los [BearerTokens] que vayamos recibiendo.
 */
val bearerTokenStorage = mutableListOf<BearerTokens>()


/**
 * Cliente HTTP encargado de hacer las autenticación de usuarios y de obtener los tokens de acceso.
 * También se encarga de realizar la generación de usuarios.
 */
@Singleton
class AuthenticationClient @Inject constructor() {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) { json() }
        expectSuccess = true
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                when {
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Unauthorized -> throw AuthenticationException()
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Conflict -> throw UserExistsException()
                    else -> {
                        exception.printStackTrace(); throw exception
                    }
                }
            }
        }
    }

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

@Singleton
class APIClient @Inject constructor() {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) { json() }

        install(Auth) {
            bearer {
                loadTokens { bearerTokenStorage.last() }
                sendWithoutRequest { request -> request.url.host == "api.omegaterapia.das.ranap.eus" }

                refreshTokens {
                    val refreshTokenInfo: TokenInfo = client.submitForm(
                        url = "https://api.omegaterapia.das.ranap.eus/auth/refresh",
                        formParameters = Parameters.build {
                            append("grant_type", "refresh_token")
                            append("refresh_token", oldTokens?.refreshToken ?: "")
                        }
                    ) { markAsRefreshTokenRequest() }.body()
                    bearerTokenStorage.add(BearerTokens(refreshTokenInfo.accessToken, oldTokens?.refreshToken!!))
                    bearerTokenStorage.last()
                }
            }
        }
    }


    suspend fun getUserProfile() {
        TODO("Por Implementar")
    }

    suspend fun uploadUserProfile(image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        httpClient.submitFormWithBinaryData(
            url = "https://api.omegaterapia.das.ranap.eus/profile/image",
            formData = formData {
                append("image", byteArray, Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=ktor_logo.png")
                })
            }
        )
    }
}
