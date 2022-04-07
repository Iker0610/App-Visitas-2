package das.omegaterapia.visits.activities.authorization.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import das.omegaterapia.visits.R
import das.omegaterapia.visits.activities.authorization.AuthViewModel
import das.omegaterapia.visits.activities.authorization.composables.LoginCard
import das.omegaterapia.visits.activities.authorization.composables.LoginSection
import das.omegaterapia.visits.activities.authorization.composables.SignInCard
import das.omegaterapia.visits.activities.authorization.composables.SignInSection
import das.omegaterapia.visits.ui.components.generic.CenteredColumn
import das.omegaterapia.visits.ui.components.generic.CenteredRow
import das.omegaterapia.visits.ui.theme.OmegaterapiaTheme
import das.omegaterapia.visits.ui.theme.getButtonShape
import das.omegaterapia.visits.utils.BiometricAuthManager
import das.omegaterapia.visits.utils.DeviceBiometricsSupport
import das.omegaterapia.visits.utils.WindowSize
import das.omegaterapia.visits.utils.WindowSizeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/*******************************************************************************
 ****                              Auth Screen                              ****
 *******************************************************************************/

/**
 * Main screen for user authentication and creation.
 *
 * It's a dynamic layout that controls every part of the Login/Sign In Activity's screen.
 *
 * When there's not enough space it only shows the Login or the Sign In and allows to switch between them.
 * If enough horizontal space is given it shows both Login and Sign In forms.
 * Data is not lost when rotating/switching/resizing etc...
 *
 * @param authViewModel [AuthViewModel] that contains required states and event calls.
 * @param windowSizeFormatClass [WindowSize] that contains relevant information in order to adjust layout to available space.
 * @param biometricSupportChecker callback that returns device's biometrics' capabilities.
 * @param onSuccessfulLogin callback for successful login event. Must get as parameter the successfully logged username.
 * @param onSuccessfulSignIn callback for successful sign in event. Must get as parameter the successfully signed in username.
 * @param onBiometricAuthRequested callback for successful login with biometrics event. Must get as parameter the successfully logged username.
 */
@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = viewModel(),
    windowSizeFormatClass: WindowSize,
    biometricSupportChecker: () -> DeviceBiometricsSupport = { DeviceBiometricsSupport.UNSUPPORTED },
    onSuccessfulLogin: (String) -> Unit = {},
    onSuccessfulSignIn: (String) -> Unit = {},
    onBiometricAuthRequested: () -> Unit = {},
) {

    /*************************************************
     **             Variables and States            **
     *************************************************/

    //-----------   Utility variables   ------------//
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    //-----------------   States   -----------------//
    var biometricSupport by rememberSaveable { mutableStateOf(biometricSupportChecker()) }

    var showSignInErrorDialog by rememberSaveable { mutableStateOf(false) }
    var showLoginErrorDialog by rememberSaveable { mutableStateOf(false) }
    var showBiometricErrorDialogState by rememberSaveable { mutableStateOf(false) }
    var showBiometricEnrollDialogState by rememberSaveable { mutableStateOf(false) }


    //-----------------   Events   -----------------//

    // On sign in clicked action
    val onSignIn: () -> Unit = {
        // Launch as coroutine in IO to avoid blocking Main(UI) thread
        coroutineScope.launch(Dispatchers.IO) {
            // Check if user has been correctly created
            val username = authViewModel.checkSignIn()
            if (username != null) {
                onSuccessfulSignIn(username)
            } else showSignInErrorDialog = authViewModel.signInUserExists
        }
    }


    // On login clicked action
    val onLogin: () -> Unit = {
        // Launch as coroutine in IO to avoid blocking Main(UI) thread
        coroutineScope.launch(Dispatchers.IO) {
            // Check if login has been successful
            val username = authViewModel.checkLogin()
            if (username != null) {
                onSuccessfulLogin(username)
            } else showLoginErrorDialog = !authViewModel.isLoginCorrect
        }
    }

    // On biometrics clicked action
    val onBiometricAuth: () -> Unit = {
        // Check device's support and act accordingly
        biometricSupport = biometricSupportChecker()
        when {
            // If there's not been a previous logged user show error dialog
            authViewModel.lastLoggedUser == null -> showBiometricErrorDialogState = true

            // If device supports biometrics but are not configured show enrollment dialog
            biometricSupport == DeviceBiometricsSupport.NOT_CONFIGURED -> showBiometricEnrollDialogState = true

            // Else if it is supported, ask for biometrics authorization
            biometricSupport != DeviceBiometricsSupport.UNSUPPORTED -> onBiometricAuthRequested()
        }
    }


    /*************************************************
     **                User Interface               **
     *************************************************/


    /*------------------------------------------------
    |                    Dialogs                     |
    ------------------------------------------------*/

    //----------   Sign In Error Dialog   ----------//
    if (showSignInErrorDialog) {
        AlertDialog(
            text = { Text(text = stringResource(R.string.existing_account_signin_dialog_title)) },
            onDismissRequest = { showSignInErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showSignInErrorDialog = false }, shape = getButtonShape()) {
                    Text(text = stringResource(id = R.string.ok_button))
                }
            },
            shape = RectangleShape
        )
    }

    //-----------   Login Error Dialog   -----------//
    if (showLoginErrorDialog) {
        AlertDialog(
            text = { Text(text = stringResource(R.string.incorrect_login_error_message)) },
            onDismissRequest = { showLoginErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showLoginErrorDialog = false }, shape = getButtonShape()) {
                    Text(text = stringResource(R.string.ok_button))
                }
            },
            shape = RectangleShape
        )
    }

    //---   Biometric Login User Error Dialog   ----//
    if (showBiometricErrorDialogState) {
        AlertDialog(
            shape = RectangleShape,
            title = { Text(text = stringResource(R.string.invalid_account_login_dialog_title), style = MaterialTheme.typography.h6) },
            text = {
                Text(
                    text = stringResource(R.string.invalid_account_login_dialog_text),
                )
            },
            onDismissRequest = { showBiometricErrorDialogState = false },
            confirmButton = {
                TextButton(onClick = { showBiometricErrorDialogState = false }, shape = getButtonShape()) {
                    Text(text = stringResource(R.string.ok_button))
                }
            }
        )
    }

    //-------   Biometric's Enroll Dialog   --------//
    if (showBiometricEnrollDialogState) {
        AlertDialog(
            shape = RectangleShape,
            title = { Text(text = stringResource(R.string.no_biometrics_enrolled_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body1) {
                        Text(text = stringResource(R.string.no_biometrics_enrolled_dialog_text_1))
                        Text(text = stringResource(R.string.no_biometrics_enrolled_dialog_text_2))
                    }
                }
            },
            onDismissRequest = { showBiometricEnrollDialogState = false },

            // If the user agrees, take them to settings in order to configure biometric authentication
            confirmButton = {
                TextButton(
                    shape = getButtonShape(),
                    onClick = {
                        showBiometricEnrollDialogState = false
                        BiometricAuthManager.makeBiometricEnroll(context)
                    }
                ) { Text(text = stringResource(R.string.enroll_button)) }
            },
            dismissButton = {
                TextButton(onClick = { showBiometricEnrollDialogState = false },
                    shape = getButtonShape()) { Text(text = stringResource(R.string.cancel_button)) }
            }
        )
    }


    /*------------------------------------------------
    |                  Main Screen                   |
    ------------------------------------------------*/
    Scaffold { padding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
        ) {

            // Check available width
            if (windowSizeFormatClass.width != WindowSizeFormat.Compact) {

                //------------   Expanded Layout   -------------//

                // Group both section in an unified Card for better aesthetic
                Card(elevation = 8.dp) {
                    CenteredRow(Modifier
                        .height(IntrinsicSize.Max)
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                    ) {
                        // Login Section
                        LoginSection(
                            authViewModel,
                            biometricSupport = biometricSupport,
                            onLogin = onLogin,
                            onBiometricAuth = onBiometricAuth
                        )

                        // Vertical Divider
                        Divider(modifier = Modifier
                            .padding(horizontal = 64.dp)
                            .fillMaxHeight()
                            .width(1.dp)
                        )

                        // Sign In Section
                        SignInSection(authViewModel, onSignIn = onSignIn)
                    }
                }
            } else {

                //-------------   Compact Layout   -------------//

                /*
                    In this case we only show one of the 2 forms.
                    Which one is shown is decided with a boolean state [authViewModel.isLogin]

                    For better user experience, we animate the visibility of both forms with horizontal slide animations.
                */
                val animationTime = 275

                // Animation spec (acts as an IF statement)
                AnimatedVisibility(
                    authViewModel.isLogin,
                    enter = slideInHorizontally(
                        initialOffsetX = { -2 * it },
                        animationSpec = tween(
                            durationMillis = animationTime,
                            easing = LinearEasing
                        )
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -2 * it },
                        animationSpec = tween(
                            durationMillis = animationTime,
                            easing = LinearEasing
                        )
                    )
                ) {
                    CenteredColumn(Modifier.width(IntrinsicSize.Max)
                    ) {
                        LoginCard(
                            authViewModel,
                            biometricSupport = biometricSupport,
                            onLogin = onLogin,
                            onBiometricAuth = onBiometricAuth
                        )

                        //--------   Switch to Sign In Button   --------//
                        Divider(modifier = Modifier.padding(top = 32.dp, bottom = 24.dp))

                        Text(text = stringResource(R.string.go_to_signin_label), style = MaterialTheme.typography.body2)
                        TextButton(onClick = authViewModel::switchScreen, shape = getButtonShape()) {
                            Text(text = stringResource(R.string.signin_button))
                        }
                    }

                }

                AnimatedVisibility(
                    !authViewModel.isLogin,
                    modifier = Modifier.fillMaxSize(),
                    enter = slideInHorizontally(
                        initialOffsetX = { 2 * it },
                        animationSpec = tween(
                            durationMillis = animationTime,
                            easing = LinearEasing
                        )
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { 2 * it },
                        animationSpec = tween(
                            durationMillis = animationTime,
                            easing = LinearEasing
                        )
                    )
                ) {
                    CenteredColumn(Modifier.width(IntrinsicSize.Max)
                    ) {
                        SignInCard(authViewModel, onSignIn = onSignIn)

                        //---------   Switch to Login Button   ---------//
                        Divider(modifier = Modifier.padding(top = 32.dp, bottom = 24.dp))

                        Text(text = stringResource(R.string.go_to_login_label), style = MaterialTheme.typography.body2)
                        TextButton(onClick = authViewModel::switchScreen, shape = getButtonShape()) {
                            Text(text = stringResource(R.string.login_button))
                        }
                    }
                }
            }
        }
    }
}

//----------------------------------------------------------------------------------------------------------

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AuthScreenPreview() {
    OmegaterapiaTheme {
        AuthScreen(windowSizeFormatClass = WindowSize(WindowSizeFormat.Compact, WindowSizeFormat.Compact, isLandscape = false))
    }
}

@Preview(widthDp = 851, heightDp = 393)
@Preview(widthDp = 851, heightDp = 393, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AuthScreenLandscapePreview() {
    OmegaterapiaTheme {
        AuthScreen(windowSizeFormatClass = WindowSize(WindowSizeFormat.Compact, WindowSizeFormat.Compact, isLandscape = true))
    }
}
