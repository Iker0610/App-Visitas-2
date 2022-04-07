package das.omegaterapia.visits.activities.authorization


import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import das.omegaterapia.visits.NotificationID
import das.omegaterapia.visits.R
import das.omegaterapia.visits.activities.authorization.screens.AnimatedSplashScreen
import das.omegaterapia.visits.activities.authorization.screens.AuthScreen
import das.omegaterapia.visits.activities.main.MainActivity
import das.omegaterapia.visits.ui.theme.OmegaterapiaTheme
import das.omegaterapia.visits.utils.BiometricAuthManager
import das.omegaterapia.visits.utils.rememberWindowSizeClass


/*******************************************************************************
 ****                         Authorization Activity                        ****
 *******************************************************************************/

/**
 * Activity for authorization related aspects (login and sign in) and intro splash screen.
 *
 * This activity will call a Main Activity when a correct authentication takes place and adds the
 * authenticated username as extra with the key "LOGGED_USERNAME".
 */
@AndroidEntryPoint
class AuthActivity : FragmentActivity() {

    /*************************************************
     **     ViewModels and other manager classes    **
     *************************************************/

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var biometricAuthManager: BiometricAuthManager


    /*************************************************
     **          Activity Lifecycle Methods         **
     *************************************************/
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize biometric authentication manager (only if it is uninitialized)
        if (!this::biometricAuthManager.isInitialized) {
            biometricAuthManager =
                BiometricAuthManager(
                    context = this,
                    authUser = authViewModel.lastLoggedUser ?: "",
                    onAuthenticationSucceeded = this::onSuccessfulLogin
                )
        }

        /*------------------------------------------------
        |                 User Interface                 |
        ------------------------------------------------*/
        setContent {
            // Apply Theme
            OmegaterapiaTheme {

                // Get processed window size
                val windowSizeClass = rememberWindowSizeClass()

                // Initialize Navigation (used for transition between splash screen and auth screen)
                val navController = rememberAnimatedNavController()
                AnimatedNavHost(
                    navController = navController,
                    startDestination = "splash_screen"
                ) {
                    //-------------   Splash Screen   --------------//
                    composable(
                        route = "splash_screen",
                        exitTransition = { fadeOut(animationSpec = tween(500)) }
                    ) {
                        AnimatedSplashScreen {
                            navController.popBackStack() // Empty the backstack so the user doesn't return to splash screen
                            navController.navigate("auth_screen")
                        }
                    }

                    //----------   Authorization Screen   ----------//
                    composable(
                        route = "auth_screen",
                        enterTransition = { fadeIn(animationSpec = tween(500)) }
                    ) {
                        AuthScreen(
                            authViewModel = authViewModel,
                            windowSizeFormatClass = windowSizeClass,
                            biometricSupportChecker = biometricAuthManager::checkBiometricSupport,
                            onSuccessfulLogin = ::onSuccessfulLogin,
                            onSuccessfulSignIn = ::onSuccessfulSignIn,
                            onBiometricAuthRequested = biometricAuthManager::submitBiometricAuthorization
                        )
                    }
                }
            }
        }
    }


    /*************************************************
     **           Login and Sign In Events          **
     *************************************************/

    /**
     * If the username has Signed In successfully, launch a local notification
     * and automatically login the new user.
     *
     * @param username Signed in username
     */
    private fun onSuccessfulSignIn(username: String) {

        // Show user created notification
        val builder = NotificationCompat.Builder(this, "AUTH_CHANNEL")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(getString(R.string.user_created_dialog_title))
            .setContentText(getString(R.string.user_created_dialog_text, username))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(NotificationID.USER_CREATED.id, builder.build())
        }

        // Log the new user
        onSuccessfulLogin(username)
    }

    /**
     * It updates the last logged user on the Datastore and launches the Main Activity
     *
     * @param username Logged in user's username
     */
    private fun onSuccessfulLogin(username: String) {
        // Set the last logged user
        authViewModel.updateLastLoggedUsername(username)

        // Open the main activity
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("LOGGED_USERNAME", username)
        }
        startActivity(intent)
        finish()
    }
}