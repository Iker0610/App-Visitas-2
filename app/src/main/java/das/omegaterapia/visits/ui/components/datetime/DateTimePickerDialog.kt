package das.omegaterapia.visits.ui.components.datetime

import android.content.Context
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.datetime.dateTimePicker
import java.time.ZonedDateTime

/**
 * Custom [DialogBehavior] that allows to pass an onDismissAction callback.
 */
class DateTimeDialogBehavior(val onDismissAction: () -> Unit) : DialogBehavior by ModalDialog {
    override fun onDismiss(): Boolean {
        this.onDismissAction()
        return false
    }
}

/**
 * Adapted date-time picker dialog for compose.
 *
 * It uses afollestad's material-dialogs library: https://github.com/afollestad/material-dialogs
 *
 * @param context Activity context.
 * @param requireFutureDateTime If true only accepts future dates.
 * @param onDismissAction Callback for onDismiss event.
 * @param onDateTimeSelected Callback for Date and Time Selected event.
 */
fun openDateTimePickerDialog(
    context: Context,
    requireFutureDateTime: Boolean = false,
    onDismissAction: () -> Unit = {},
    onDateTimeSelected: (ZonedDateTime) -> Unit,
) {
    MaterialDialog(context, DateTimeDialogBehavior(onDismissAction)).show {
        dateTimePicker(requireFutureDateTime = requireFutureDateTime) { _, dateTime ->
            onDateTimeSelected(ZonedDateTime.ofInstant(dateTime.toInstant(), dateTime.timeZone.toZoneId()))
        }
    }
}