package das.omegaterapia.visits.activities.authorization.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
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
import das.omegaterapia.visits.utils.canBeValidUsername


/*******************************************************************************
 ****                              Sign In Card                              ****
 *******************************************************************************/

/**
 * UI element that defines the login form.
 *
 * @param authViewModel [AuthViewModel] that contains required states and event calls.
 * @param modifier
 * @param onSignIn Callback for sign in event.
 */
@Composable
fun SignInCard(authViewModel: AuthViewModel, modifier: Modifier = Modifier, onSignIn: () -> Unit = {}) {
    Card(modifier = modifier, elevation = 8.dp) {
        SignInSection(authViewModel, Modifier.padding(horizontal = 32.dp, vertical = 16.dp), onSignIn)
    }
}


/*******************************************************************************
 ****                           Sign In Section                             ****
 *******************************************************************************/

/**
 * UI element that defines the sign in form.
 *
 * @param authViewModel [AuthViewModel] that contains required states and event calls.
 * @param modifier
 * @param onSignIn Callback for sign in event.
 */
@Composable
fun SignInSection(authViewModel: AuthViewModel, modifier: Modifier = Modifier, onSignIn: () -> Unit = {}) {
    CenteredColumn(
        modifier = modifier.width(IntrinsicSize.Min)
    ) {
        //-------------   Sign In Title   --------------//
        Text(text = stringResource(R.string.sign_in), style = MaterialTheme.typography.h5)

        Spacer(Modifier.height(8.dp))


        //--------   User and Password Fields   --------//

        ValidatorOutlinedTextField(
            modifier = Modifier.widthIn(max = 280.dp),

            value = authViewModel.signInUsername,
            onValueChange = { if (canBeValidUsername(it)) authViewModel.signInUsername = it; authViewModel.signInUserExists = false },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text(text = stringResource(R.string.username)) },
            isValid = authViewModel.signInUsername.isBlank() || (authViewModel.isSignInUsernameValid && !authViewModel.signInUserExists),
            ignoreFirstTime = true
        )

        Column {
            PasswordField(
                modifier = Modifier.widthIn(max = 280.dp),

                value = authViewModel.signInPassword,
                onValueChange = authViewModel::signInPassword::set,
                isValid = authViewModel.signInPassword.isBlank() || authViewModel.isSignInPasswordValid,
                ignoreFirstTime = true
            )
            if (authViewModel.signInPassword.isNotBlank() && !authViewModel.isSignInPasswordValid) {
                Text(
                    text = stringResource(R.string.invalid_new_password_error_text),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        PasswordField(
            modifier = Modifier.widthIn(max = 280.dp),

            value = authViewModel.signInConfirmationPassword,
            onValueChange = authViewModel::signInConfirmationPassword::set,
            isValid = authViewModel.signInConfirmationPassword.isBlank() || authViewModel.isSignInPasswordConfirmationValid,
            ignoreFirstTime = true,

            label = { Text(text = stringResource(R.string.confirm_password_field_label)) },
            placeholder = { Text(text = stringResource(R.string.confirm_password_field_placeholder)) },
        )

        Divider(Modifier.padding(top = 24.dp, bottom = 16.dp))

        //-------------   Sign In Button   -------------//
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSignIn,
            shape = getButtonShape(),

            // We only enable the button if username and password have valid values (does not check if username already exist)
            enabled = authViewModel.isSignInUsernameValid && authViewModel.isSignInPasswordConfirmationValid
        ) {
            Text(text = stringResource(R.string.signin_button))
        }
    }
}

//----------------------------------------------------------------------------------

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SignInCardPreview() {
    OmegaterapiaTheme {
        Surface {
            SignInCard(viewModel())
        }
    }
}