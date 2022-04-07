package das.omegaterapia.visits.ui.components.datetime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


/**
 * Personalized OutlinedTextField for date and time.
 *
 * It doesn't allow the user to enter the date with the keyboard and shows a date-time picker dialog.
 *
 * @param modifier
 * @param date Current date value.
 * @param onDateTimeSelected Callback for onSelection event.
 * @param dateFormatPattern Pattern used to show the current selected Date Time in the field.
 * @param requireFutureDateTime If true, only accepts dates after "now"
 * @param label
 * @param placeholder
 * @param leadingIcon Socket for an icon at the start of the field.
 * @param trailingIcon Socket for an icon at the end of the field.
 * @param enabled
 */
@Composable
fun OutlinedDateTimeField(
    modifier: Modifier = Modifier,

    date: ZonedDateTime = ZonedDateTime.now(),
    onDateTimeSelected: (ZonedDateTime) -> Unit = {},
    dateFormatPattern: String = "dd/MM/yyyy hh:mm",
    requireFutureDateTime: Boolean = false,

    label: @Composable () -> Unit = { Text(text = "Date-Time") },
    placeholder: @Composable (() -> Unit)? = { Text(text = dateFormatPattern) },
    leadingIcon: @Composable (() -> Unit)? = { Icon(Icons.Default.Event, null) },
    trailingIcon: @Composable (() -> Unit)? = null,

    enabled: Boolean = true,
) {
    //---------------   Variables   ----------------//

    val context = LocalContext.current

    // Focus manager to open the dialog and remove focus once user finishes it's selection
    val focusManager = LocalFocusManager.current

    // Formatted date as string
    val dateTimeText = date.format(DateTimeFormatter.ofPattern(dateFormatPattern))!!


    //-------------------   UI   -------------------//
    OutlinedTextField(
        modifier = modifier.onFocusChanged {
            // If the field is focused open the picker dialog
            if (it.isFocused) {
                openDateTimePickerDialog(context, requireFutureDateTime, onDismissAction = { focusManager.clearFocus() }) { dateTime ->
                    onDateTimeSelected(dateTime)
                    // Once the user finishes their selection clear the focus from this field
                    focusManager.clearFocus()
                }
            }
        },

        label = label,
        leadingIcon = leadingIcon,
        placeholder = placeholder,

        value = dateTimeText,
        onValueChange = {},

        trailingIcon = trailingIcon,

        readOnly = true,
        singleLine = true,
        maxLines = 1,

        enabled = enabled
    )
}

/**
 * Custom pair of synchronized TextFields for date and time.
 *
 * They are 2 separate TextFields that share the same state and date, are focused at the same time, etc.
 * Date TextField takes 3/5 of the max width given to this composable and Time TextField takes the remaining space (2/5).
 * This option it's much more clear and aesthetic than only one TextField with both: date and time.
 */
@Composable
fun AlternativeOutlinedDateTimeField(
    modifier: Modifier = Modifier,

    date: ZonedDateTime = ZonedDateTime.now(),
    onDateTimeSelected: (ZonedDateTime) -> Unit = {},
    dateFormatPattern: String = "dd/MM/yyyy",
    timeFormatPattern: String = "hh:mm",
    requireFutureDateTime: Boolean = false,

    dateLabel: @Composable () -> Unit = { Text(text = "Date") },
    datePlaceholder: @Composable (() -> Unit)? = { Text(text = dateFormatPattern) },
    dateLeadingIcon: @Composable (() -> Unit)? = { Icon(Icons.Default.Event, contentDescription = "Date") },
    dateTrailingIcon: @Composable (() -> Unit)? = null,

    timeLabel: @Composable () -> Unit = { Text(text = "Time") },
    timePlaceholder: @Composable (() -> Unit)? = { Text(text = timeFormatPattern) },
    timeLeadingIcon: @Composable (() -> Unit)? = { Icon(Icons.Default.Schedule, contentDescription = "Time") },
    timeTrailingIcon: @Composable (() -> Unit)? = null,

    enabled: Boolean = true,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        //---------------   Date Field   ---------------//
        OutlinedDateTimeField(
            modifier = Modifier.weight(1.5f),

            date = date,
            onDateTimeSelected = onDateTimeSelected,
            dateFormatPattern = dateFormatPattern,
            requireFutureDateTime = requireFutureDateTime,

            label = dateLabel,
            placeholder = datePlaceholder,
            leadingIcon = dateLeadingIcon,
            trailingIcon = dateTrailingIcon,

            enabled = enabled,
        )

        //---------------   Time Field   ---------------//
        OutlinedDateTimeField(
            modifier = Modifier.weight(1f),

            date = date,
            onDateTimeSelected = onDateTimeSelected,
            dateFormatPattern = timeFormatPattern,
            requireFutureDateTime = requireFutureDateTime,

            label = timeLabel,
            placeholder = timePlaceholder,
            leadingIcon = timeLeadingIcon,
            trailingIcon = timeTrailingIcon,

            enabled = enabled,
        )
    }
}