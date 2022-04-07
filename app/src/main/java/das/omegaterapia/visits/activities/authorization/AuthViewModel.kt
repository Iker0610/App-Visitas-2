package das.omegaterapia.visits.activities.authorization

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import das.omegaterapia.visits.model.entities.AuthUser
import das.omegaterapia.visits.model.repositories.ILoginRepository
import das.omegaterapia.visits.utils.compareHash
import das.omegaterapia.visits.utils.hash
import das.omegaterapia.visits.utils.isValidPassword
import das.omegaterapia.visits.utils.isValidUsername
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


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
    val lastLoggedUser: String?
        get() = runBlocking { return@runBlocking loginRepository.getLastLoggedUser() } // Get last logged user synchronously
            .let {
                // Check if there's a previus logged user and if that user still exist (if not returns null)
                if (it != null && runBlocking { return@runBlocking loginRepository.getUserPassword(it) } != null) {
                    it
                } else null
            }

    // Current Screen (login/sign in) to show
    var isLogin: Boolean by mutableStateOf(true)
        private set


    // Login States
    var isLoginCorrect by mutableStateOf(true)
        private set

    var loginUsername by mutableStateOf(lastLoggedUser ?: "")
    var loginPassword by mutableStateOf("")


    // Sign In States
    var signInUsername by mutableStateOf("")
    var signInPassword by mutableStateOf("")
    var signInConfirmationPassword by mutableStateOf("")

    val isSignInUsernameValid by derivedStateOf { isValidUsername(signInUsername) }
    val isSignInPasswordValid by derivedStateOf { isValidPassword(signInPassword) }
    val isSignInPasswordConfirmationValid by derivedStateOf { isSignInPasswordValid && signInPassword == signInConfirmationPassword }

    var signInUserExists by mutableStateOf(false)


    /*************************************************
     **                    Events                   **
     *************************************************/
    fun switchScreen() {
        isLogin = !isLogin
    }


    //--------------   Login Events   --------------//

    /**
     * Checks if the user defined with [loginUsername] and [loginPassword] exists and it's correct.
     *
     * @return Logged user's username if everything is correct or null otherwise.
     */
    suspend fun checkLogin(): String? {
        val username = loginUsername
        isLoginCorrect = loginPassword.compareHash(loginRepository.getUserPassword(username))
        return if (isLoginCorrect) username else null
    }

    // Update las logged username
    fun updateLastLoggedUsername(username: String) = runBlocking {
        loginRepository.setLastLoggedUser(username)
    }


    //-------------   Sign In Events   -------------//

    /**
     * Sign in the new user.
     *
     * Checks if [signInUsername] and both passwords are correct.
     * If [signInUsername] doesn't exist the method created the user in the [loginRepository].
     *
     * @return Created user's username if everything went right, null otherwise
     */
    suspend fun checkSignIn(): String? {
        if (isSignInUsernameValid && isSignInPasswordConfirmationValid) {
            val newUser = AuthUser(signInUsername, signInPassword.hash())
            val signInCorrect = loginRepository.createUser(newUser)
            signInUserExists = !signInCorrect
            return if (signInCorrect) newUser.username else null
        }
        return null
    }
}