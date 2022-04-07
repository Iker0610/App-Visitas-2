package das.omegaterapia.visits.activities.authorization.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import das.omegaterapia.visits.R
import das.omegaterapia.visits.activities.authorization.AuthViewModel
import das.omegaterapia.visits.ui.components.form.PasswordField
import das.omegaterapia.visits.ui.components.form.ValidatorOutlinedTextField
import das.omegaterapia.visits.ui.components.generic.CenteredColumn
import das.omegaterapia.visits.ui.theme.OmegaterapiaTheme
import das.omegaterapia.visits.ui.theme.getButtonShape
import das.omegaterapia.visits.utils.DeviceBiometricsSupport
import das.omegaterapia.visits.utils.canBeValidUsername

/*******************************************************************************
 ****                               Login Card                              ****
 *******************************************************************************/

/**
 * UI element that envelops a Login Section in a Card composable.
 *
 * @param authViewModel [AuthViewModel] that contains required states and event calls.
 * @param modifier
 * @param biometricSupport instance of [DeviceBiometricsSupport] indicating this device's biometric capabilities.
 * @param onLogin Callback for login event.
 * @param onBiometricAuth Callback for login with biometric authentication.
 */
@Composable
fun LoginCard(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    biometricSupport: DeviceBiometricsSupport = DeviceBiometricsSupport.UNSUPPORTED,
    onLogin: () -> Unit = {},
    onBiometricAuth: () -> Unit = {},
) {
    Card(modifier = modifier, elevation = 8.dp) {
        LoginSection(
            authViewModel = authViewModel,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
            biometricSupport = biometricSupport,
            onLogin = onLogin,
            onBiometricAuth = onBiometricAuth
        )
    }
}


/*******************************************************************************
 ****                             Login Section                             ****
 *******************************************************************************/

/**
 * UI element that defines the login form.
 *
 * @param authViewModel [AuthViewModel] that contains required states and event calls.
 * @param modifier
 * @param biometricSupport instance of [DeviceBiometricsSupport] indicating this device's biometric capabilities.
 * @param onLogin Callback for login event.
 * @param onBiometricAuth Callback for login with biometric authentication.
 */
@Composable
fun LoginSection(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    biometricSupport: DeviceBiometricsSupport = DeviceBiometricsSupport.UNSUPPORTED,
    onLogin: () -> Unit = {},
    onBiometricAuth: () -> Unit = {},
) {
    CenteredColumn(
        modifier = modifier.width(IntrinsicSize.Min)
    ) {
        //--------------   Login Title   ---------------//
        Text(text = stringResource(R.string.login), style = MaterialTheme.typography.h5)

        Spacer(Modifier.height(8.dp))

        //--------   User and Password Fields   --------//
        ValidatorOutlinedTextField(
            modifier = Modifier.widthIn(max = 280.dp),
            value = authViewModel.loginUsername,
            onValueChange = { if (canBeValidUsername(it)) authViewModel.loginUsername = it }, // Change text if it's valid only
            leadingIcon = { Icon(Icons.Filled.Person, null) },
            label = { Text(text = stringResource(R.string.username)) },
            isValid = authViewModel.isLoginCorrect, // Check if text is valid in order to show textfield's error state
            ignoreFirstTime = true
        )

        PasswordField(
            modifier = Modifier.widthIn(max = 280.dp),
            value = authViewModel.loginPassword,
            onValueChange = authViewModel::loginPassword::set,
            isValid = authViewModel.isLoginCorrect,
            ignoreFirstTime = true
        )

        Divider(Modifier.padding(top = 24.dp, bottom = 16.dp))

        //--------------   Login Button   --------------//
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onLogin,
            shape = getButtonShape(),

            // We only enable the button if user and password have possible valid values (does not check if user and pass are in database)
            enabled = authViewModel.loginUsername.isNotBlank() && authViewModel.loginPassword.isNotBlank()
        ) {
            Text(text = stringResource(R.string.login_button))
        }

        Spacer(Modifier.height(8.dp))

        //-----------   Biometrics Button   ------------//

        // Only showed if the device has some kind of biometric capability
        if (biometricSupport != DeviceBiometricsSupport.UNSUPPORTED) {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onBiometricAuth,
                shape = getButtonShape()
            ) {
                Icon(Icons.Filled.Fingerprint, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.biometrics_button))
            }
        }
    }
}


//----------------------------------------------------------------------------------

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginCardPreview() {
    OmegaterapiaTheme {
        Surface {
            LoginCard(viewModel())
        }
    }
}
