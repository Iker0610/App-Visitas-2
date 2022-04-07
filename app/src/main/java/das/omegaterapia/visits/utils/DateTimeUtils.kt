package das.omegaterapia.visits.utils

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import das.omegaterapia.visits.R
import das.omegaterapia.visits.ui.components.generic.CenteredRow
import das.omegaterapia.visits.ui.theme.getButtonShape
import das.omegaterapia.visits.utils.TemporalConverter.Companion.multipleDaysConverters
import das.omegaterapia.visits.utils.TemporalConverter.Companion.oneDayConverters
import das.omegaterapia.visits.utils.TemporalConverter.WEEK
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SUNDAY
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.*


/*******************************************************************************
 ****              Date Time Grouping-Formatter-Converter Utils             ****
 *******************************************************************************/


/**
 * This class gives some time formatters in order to group them.
 *
 * For example if [WEEK] converted picked, all the dates of the same week (monday, tuesday, etc...) will be parsed to the monday date.
 * This allows grouping dates and have a header (common date representation) for them.
 *
 * [TemporalConverter] that are in [oneDayConverters] are intended to use on list that contains items of the SAME ONE DAY, not from different days.
 * [TemporalConverter] that are in [multipleDaysConverters] can be used without conditions having always a good result.
 *
 *
 * @property example Example of the resulting conversion.
 * @property converter Function that converts the given [ZonedDateTime] to the formatted string.
 */
enum class TemporalConverter(val example: String, val converter: (ZonedDateTime) -> String) {
    /*------------------------------------------------
    |             Enumeration Instances              |
    ------------------------------------------------*/
    MONTH("2022 - January", { it.with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy - MMMM")).uppercase() }),
    WEEK("2022 FEB 28 - MAR 06", ::getWeek),
    DAY("2000 - JAN 02, Tuesday", ::getDay),
    HOUR("17:45", ::getHour),
    HOUR_WITH_DAY("Monday 26 - 17:45", {
        getHour(it, "EEEE dd - HH:mm")
            .replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() }
    }),
    HOUR_WITH_FULL_DATE("2000 JAN 02 (Mon) - 17:45", { getHour(it, "yyyy MMM dd (EE) - HH:mm") })
    ;


    /*------------------------------------------------
    |         Enumeration Instance's Methods         |
    ------------------------------------------------*/

    /**
     * Function to get the [TemporalConverter] config-name to show to user from R.string.
     * This cannot be a [TemporalConverter] attribute cause [Context] is needed for it.
     */
    fun configName(context: Context): String {
        val titleStringID = context.resources.getIdentifier("${this.name.lowercase()}_temporal_converter", "string", context.packageName)
        return context.getString(titleStringID)
    }

    /**
     * Function to map a list of items containing a [ZonedDateTime] to a map.
     *
     * This map's keys are the common representation of the dates in the given list applied the caller [TemporalConverter]
     *
     * @param T Type of the items in the original list
     * @param list List with items
     * @param key Callback to get a [ZonedDateTime] from a [T] typed item.
     * @return Map containing a key for each unique group created with the [TemporalConverter], and the list of items that belong to that group.
     */
    fun <T> groupDates(list: List<T>, key: (T) -> ZonedDateTime): Map<String, List<T>> = list.groupBy { this.converter(key(it)) }


    /*------------------------------------------------
    |                Static Constants                |
    ------------------------------------------------*/
    companion object {
        val oneDayDefault = HOUR_WITH_DAY
        val multipleDayDefault = WEEK

        val oneDayConverters = listOf(HOUR, HOUR_WITH_DAY, HOUR_WITH_FULL_DATE)
        val multipleDaysConverters = listOf(MONTH, WEEK, DAY)
    }
}

//--------------------------------------------------------------------------------------------------------------------------------------------------//

/*------------------------------------------------
|  Big TemporalConverter's Converter Functions   |
------------------------------------------------*/

private fun getWeek(dateTime: ZonedDateTime): String {

    // Get monday of that week
    val mondayOfWeek = dateTime.with(TemporalAdjusters.previousOrSame(MONDAY))
    val initialString = mondayOfWeek.format(DateTimeFormatter.ofPattern("yyyy MMM dd"))

    // Get sunday of that week and concatenate both
    return (initialString + mondayOfWeek.with(TemporalAdjusters.next(SUNDAY)).format(DateTimeFormatter.ofPattern(" - MMM dd"))).uppercase()
}

private fun getDay(dateTime: ZonedDateTime): String {
    val date = dateTime.with(TemporalAdjusters.ofDateAdjuster { d: LocalDate -> d })
    val dateString = date.format(DateTimeFormatter.ofPattern("yyyy - MMM dd")).uppercase()
    val dayOfWeek = date.format(DateTimeFormatter.ofPattern("EEEE"))
        .replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() }

    return "$dateString, $dayOfWeek"
}

private fun getHour(dateTime: ZonedDateTime, pattern: String = "HH:mm"): String =
    dateTime.truncatedTo(ChronoUnit.HOURS).format(DateTimeFormatter.ofPattern(pattern))

//--------------------------------------------------------------------------------------------------------------------------------------------------//


/*************************************************
 **       TemporalConverter Picker Dialogs      **
 *************************************************/


/*------------------------------------------------
|        One-Day Converters Picker Dialog        |
------------------------------------------------*/
@Composable
fun DayConverterPickerDialog(
    selectedConverter: String,
    onConverterSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    title: String,
) {
    ConverterPickerDialog(
        formatList = oneDayConverters,
        selectedConverter = selectedConverter,
        onConverterSelected = onConverterSelected,
        title = title,
        onDismiss = onDismiss
    )
}


/*------------------------------------------------
|     Multiple-Days Converters Picker Dialog     |
------------------------------------------------*/
@Composable
fun MultipleDaysConverterPickerDialog(
    selectedConverter: String,
    onConverterSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    title: String,
) {
    ConverterPickerDialog(
        formatList = multipleDaysConverters,
        selectedConverter = selectedConverter,
        onConverterSelected = onConverterSelected,
        title = title,
        onDismiss = onDismiss
    )
}


/*------------------------------------------------
|      TemporalConverter Main Picker Dialog      |
------------------------------------------------*/

/**
 * Custom dialog with a scrollable list in middle that allows the user to pick one of the available [TemporalConverter].
 *
 * It follows Material Design's Confirmation Dialog design pattern, as stated in:
 * https://material.io/components/dialogs#confirmation-dialog
 *
 * @param title Title of the Dialog.
 * @param selectedConverter Current selected [TemporalConverter].
 * @param formatList List of converters to allow the user to choose. Usually [oneDayConverters] or [multipleDaysConverters].
 * @param onConverterSelected Callback for onConverterSelected event.
 * @param onDismiss Callback for dismiss event.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ConverterPickerDialog(
    formatList: List<TemporalConverter>,
    selectedConverter: String,
    onConverterSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    title: String,
) {
    /*------------------------------------------------
    |                     States                     |
    ------------------------------------------------*/

    var selected by rememberSaveable { mutableStateOf(selectedConverter) }


    /*------------------------------------------------
    |                 User Interface                 |
    ------------------------------------------------*/
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RectangleShape,
            color = MaterialTheme.colors.surface,
            contentColor = contentColorFor(SnackbarDefaults.backgroundColor),
        ) {
            Column {

                //--------------   Dialog Title   --------------//
                Column(Modifier
                    .padding(horizontal = 24.dp)
                    .height(64.dp), verticalArrangement = Arrangement.Center) {
                    Text(text = title, style = MaterialTheme.typography.h6)
                }

                Divider()

                //---------   Dialog Content (List)   ----------//
                Column(Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                ) {
                    formatList.forEach { format ->
                        ListItem(
                            modifier = Modifier.clickable { selected = format.name },
                            trailing = { Checkbox(checked = selected == format.name, onCheckedChange = { selected = format.name }) },
                            text = { Text(text = format.configName(LocalContext.current), style = MaterialTheme.typography.body1) },
                            secondaryText = { Text(text = format.example) }
                        )
                    }
                }

                //-------------   Dialog Buttons   -------------//
                Divider()

                CenteredRow(Modifier
                    .fillMaxWidth()
                    .padding(8.dp), horizontalArrangement = Arrangement.End) {
                    CenteredRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        // Cancel TextButton
                        TextButton(onClick = onDismiss, shape = getButtonShape()) { Text(text = stringResource(R.string.cancel_button)) }

                        // Apply TextButton
                        TextButton(onClick = { onConverterSelected(selected) }, shape = getButtonShape()) {
                            Text(text = stringResource(R.string.apply_button))
                        }
                    }
                }
            }
        }
    }
}