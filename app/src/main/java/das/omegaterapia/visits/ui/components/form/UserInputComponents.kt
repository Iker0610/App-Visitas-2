package das.omegaterapia.visits.ui.components.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import das.omegaterapia.visits.R
import das.omegaterapia.visits.ui.theme.getButtonShape
import das.omegaterapia.visits.utils.canBePassword

/**
 * Custom OutlinedTextField that takes a boolean parameter indicating if the current value is valid.
 *
 * If the value is not valid the TextField appearance changes (red border) to reflect it
 * This field allows by default to not apply the validation before the user interacts for the first time with the TextField.
 * With the [ignoreFirstTime] parameter it's posible to disable this feature.
 *
 * @param value Field's current value.
 * @param onValueChange Callback for onValueChange event, it must take the new value as parameter.
 * @param isValid If the field's current value is valid. If false the field changes it's appearance to reflect this state.
 * @param ignoreFirstTime Apply [isValid] even if the user hasn't interacted with the field yet.
 */
@Composable
fun ValidatorOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,

    modifier: Modifier = Modifier,

    isValid: Boolean = true,
    ignoreFirstTime: Boolean = false,

    label: @Composable () -> Unit,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,

    singleLine: Boolean = true,
    maxLines: Int = 1,

    enabled: Boolean = true,
    readOnly: Boolean = false,

    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var isFirstTime: Boolean by rememberSaveable { mutableStateOf(true) }
    if (ignoreFirstTime) isFirstTime = false

    OutlinedTextField(
        value = value,
        onValueChange = { isFirstTime = false; onValueChange(it) },

        isError = !(isFirstTime || isValid),

        modifier = modifier,

        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,

        singleLine = singleLine,
        maxLines = maxLines,

        enabled = enabled,
        readOnly = readOnly,

        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )
}


/**
 * Predefined and styled password field.
 *
 * It uses a [ValidatorOutlinedTextField] and thus, it has the same capabilities.
 *
 * @param value Field's current value.
 * @param onValueChange Callback for onValueChange event, it must take the new value as parameter.
 * @param leadingIcon Socket for field's icon at the start.
 * @param isValid If the field's current value is valid. If false the field changes it's appearance to reflect this state.
 * @param ignoreFirstTime Apply [isValid] even if the user hasn't interacted with the field yet.
 */
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit) = { Text(stringResource(R.string.password_label)) },
    placeholder: @Composable (() -> Unit)? = { Text(stringResource(R.string.password_placeholder)) },
    leadingIcon: @Composable (() -> Unit)? = { Icon(Icons.Filled.VpnKey, contentDescription = stringResource(R.string.password_label)) },
    isValid: Boolean = true,
    ignoreFirstTime: Boolean = false,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    ValidatorOutlinedTextField(
        value = value,
        onValueChange = { if (canBePassword(it)) onValueChange(it) }, // Update value only if the new value can be a valid password.

        modifier = modifier,

        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),

        label = label,
        placeholder = placeholder,
        isValid = isValid,
        ignoreFirstTime = ignoreFirstTime,

        leadingIcon = leadingIcon,

        // Trailing icon to show/hide password
        trailingIcon = {
            val description = if (passwordVisible) stringResource(R.string.password_icon_visible) else stringResource(R.string.password_icon_hidden)

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                AnimatedVisibility(passwordVisible, enter = fadeIn(), exit = fadeOut()) {
                    Icon(imageVector = Icons.Filled.Visibility, description, tint = MaterialTheme.colors.secondary)
                }
                AnimatedVisibility(!passwordVisible, enter = fadeIn(), exit = fadeOut()) {
                    Icon(imageVector = Icons.Filled.VisibilityOff, description)
                }
            }
        }
    )
}


// Predesigned item to have a material textbutton with an icon at the start
@Composable
fun TextIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = null,
    shape: Shape = getButtonShape(),
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    icon: ImageVector,
    text: String,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
    ) {
        Icon(icon, null, Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(2.dp))
        Text(text = text)
    }
}