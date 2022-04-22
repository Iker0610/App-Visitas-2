package das.omegaterapia.visits.activities.authorization

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import das.omegaterapia.visits.activities.authorization.BiometricAuthenticationStatus.*
import das.omegaterapia.visits.model.entities.AuthUser
import das.omegaterapia.visits.model.repositories.ILoginRepository
import das.omegaterapia.visits.utils.isValidPassword
import das.omegaterapia.visits.utils.isValidUsername
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


/**
 * Enum class representing possible biometric authentication status.
 *
 * [AUTHENTICATED] - Authentication has been Successful
 * [NO_CREDENTIALS] - There are no credentials
 * [CREDENTIALS_ERROR] - Credentials are not valid
 * [ERROR] - Another error has occurred and authentication has not been possible
 * [NOT_AUTHENTICATED_YET] - Neutral state
 */
enum class BiometricAuthenticationStatus {
    AUTHENTICATED,
    NO_CREDENTIALS,
    CREDENTIALS_ERROR,
    ERROR,
    NOT_AUTHENTICATED_YET

}


/*******************************************************************************
 ****                       Authentication View Model                       ****
 *******************************************************************************/

/**
 * Hilt ViewModel for Authentication screens.
 *
 * It has every state needed for the screens and it's in charge of updating the database.
 *
 * @property loginRepository Implementation of [ILoginRepository] that has necessary methods to save and fetch model data needed for authentication.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(private val loginRepository: ILoginRepository) : ViewModel() {


    /*************************************************
     **                    States                   **
     *************************************************/

    // Property that has the last logged user (if it exists else null)
    val lastLoggedUser: AuthUser? = runBlocking { return@runBlocking loginRepository.getLastLoggedUser() } // Get last logged user synchronously


    // Biometric authentication state
    var biometricAuthenticationStatus: BiometricAuthenticationStatus by mutableStateOf(if (lastLoggedUser != null) NOT_AUTHENTICATED_YET else NO_CREDENTIALS)


    // Current Screen (login/sign in) to show
    var isLogin: Boolean by mutableStateOf(true)
        private set

    var isLoginCorrect by mutableStateOf(true)
        private set

    var loginUsername by mutableStateOf(lastLoggedUser?.username ?: "")
    var loginPassword by mutableStateOf("")


    // Sign In States
    var signInUsername by mutableStateOf("")
    var signInPassword by mutableStateOf("")
    var signInConfirmationPassword by mutableStateOf("")

    val isSignInUsernameValid by derivedStateOf { isValidUsername(signInUsername) }
    val isSignInPasswordValid by derivedStateOf { isValidPassword(signInPassword) }
    val isSignInPasswordConfirmationValid by derivedStateOf { isSignInPasswordValid && signInPassword == signInConfirmationPassword }

    var signInUserExists by mutableStateOf(false)


    // Property that defines if a background task that must block the UI is on course
    var backgroundBlockingTaskOnCourse: Boolean by mutableStateOf(false)

    /*************************************************
     **                    Events                   **
     *************************************************/
    fun switchScreen() {
        isLogin = !isLogin
    }


    //--------------   Login Events   --------------//

    /**
     * Checks if the given [authUser] is a valid user in the system.
     */
    suspend fun checkUserLogin(authUser: AuthUser): Boolean {
        backgroundBlockingTaskOnCourse = true
        return loginRepository.authenticateUser(authUser)
    }


    /**
     * Checks credentials of [lastLoggedUser] and updates [biometricAuthenticationStatus] depending on the result.
     * Logged [AuthUser] if everything is correct or null otherwise.
     */
    suspend fun checkBiometricLogin(): AuthUser? {
        if (biometricAuthenticationStatus != NO_CREDENTIALS) {
            biometricAuthenticationStatus =
                try {
                    if (checkUserLogin(lastLoggedUser!!)) AUTHENTICATED else CREDENTIALS_ERROR
                } catch (e: Exception) {
                    ERROR
                }
        }
        return if (biometricAuthenticationStatus == AUTHENTICATED) lastLoggedUser else null
    }


    /**
     * Checks if the user defined with [loginUsername] and [loginPassword] exists and it's correct.
     *
     * @return Logged [AuthUser] if everything is correct or null otherwise.
     * @throws Exception if a non credential error occurs
     */
    @Throws(Exception::class)
    suspend fun checkUserPasswordLogin(): AuthUser? {
        val user = AuthUser(loginUsername, loginPassword)
        isLoginCorrect = checkUserLogin(user)
        return if (isLoginCorrect) user else null
    }


    // Update las logged username
    fun updateLastLoggedUsername(user: AuthUser) = runBlocking {
        loginRepository.setLastLoggedUser(user)
    }


    //-------------   Sign In Events   -------------//

    /**
     * Sign in the new user.
     *
     * Checks if [signInUsername] and both passwords are correct.
     * If [signInUsername] doesn't exist the method created the user in the [loginRepository].
     *
     * @return Created [AuthUser] if everything went right, null otherwise
     */
    suspend fun checkSignIn(): AuthUser? {
        backgroundBlockingTaskOnCourse = true

        if (isSignInUsernameValid && isSignInPasswordConfirmationValid) {
            val newUser = AuthUser(signInUsername, signInPassword)
            val signInCorrect = loginRepository.createUser(newUser)
            signInUserExists = !signInCorrect
            return if (signInCorrect) newUser else null
        }
        return null
    }
}