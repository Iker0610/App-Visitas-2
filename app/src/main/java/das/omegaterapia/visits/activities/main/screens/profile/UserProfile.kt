package das.omegaterapia.visits.activities.main.screens.profile

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import das.omegaterapia.visits.R
import das.omegaterapia.visits.activities.main.VisitsViewModel
import das.omegaterapia.visits.ui.components.form.FormSubsection
import das.omegaterapia.visits.ui.components.generic.CenteredColumn
import das.omegaterapia.visits.ui.components.navigation.BackArrowTopBar
import das.omegaterapia.visits.utils.DayConverterPickerDialog
import das.omegaterapia.visits.utils.LanguagePickerDialog
import das.omegaterapia.visits.utils.MultipleDaysConverterPickerDialog
import das.omegaterapia.visits.utils.TemporalConverter
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/*******************************************************************************
 ****                     Account Screen User Interface                     ****
 *******************************************************************************/

/**
 * It defines the user's account interface:
 *
 *  - A top app bar that has a [title] and back-arrow button that when clicked calls [onBackPressed]
 *  - A header section with the logo and the username
 *  - A section to choose preferences:
 *      - Preferred language
 *      - Preferred date grouping for Today's Visits screen
 *      - Preferred date grouping for All Visits and VIP Visits screens
 *
 *
 * Requires 2 viewmodels: [visitsViewModel] and [preferencesViewModel
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserProfileScreen(
    title: String,
    visitsViewModel: VisitsViewModel,
    preferencesViewModel: PreferencesViewModel = hiltViewModel(),
    onBackPressed: () -> Unit = {},
) {
    /*************************************************
     **                Event Handlers               **
     *************************************************/

    BackHandler(onBack = onBackPressed)


    /*************************************************
     **             Variables and States            **
     *************************************************/

    //-----------   Utility variables   ------------//
    val context = LocalContext.current

    /*------------------------------------------------
    |                     States                     |
    ------------------------------------------------*/

    //--------------   Preferences   ---------------//
    val prefLanguage by preferencesViewModel.prefLang.collectAsState(initial = preferencesViewModel.currentSetLang)
    val prefOneDayConverter by preferencesViewModel.prefOneDayConverter.collectAsState(initial = TemporalConverter.oneDayDefault.name)
    val prefMultipleDayConverter by preferencesViewModel.prefMultipleDayConverter.collectAsState(initial = TemporalConverter.multipleDayDefault.name)

    //---------   Dialog Control States   ----------//
    var showSelectLangDialog by rememberSaveable { mutableStateOf(false) }
    var showDayConverterDialog by rememberSaveable { mutableStateOf(false) }
    var showMultipleDaysConverterDialog by rememberSaveable { mutableStateOf(false) }


    /*************************************************
     **                User Interface               **
     *************************************************/

    Scaffold(topBar = { BackArrowTopBar(title, onBackPressed) }) {
        CenteredColumn(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
        ) {
            /*------------------------------------------------
            |                    Dialogs                     |
            ------------------------------------------------*/

            if (showSelectLangDialog) {
                LanguagePickerDialog(
                    title = stringResource(R.string.select_lang_dialog_title),
                    selectedLanguage = prefLanguage,
                    onLanguageSelected = { preferencesViewModel.changeLang(it, context); showSelectLangDialog = false },
                    onDismiss = { showSelectLangDialog = false }
                )
            }

            if (showDayConverterDialog) {
                DayConverterPickerDialog(
                    title = stringResource(R.string.select_date_grouping_dialog_title),
                    selectedConverter = prefOneDayConverter,
                    onConverterSelected = { preferencesViewModel.setOneDayConverterPreference(it); showDayConverterDialog = false },
                    onDismiss = { showDayConverterDialog = false }
                )
            }

            if (showMultipleDaysConverterDialog) {
                MultipleDaysConverterPickerDialog(
                    title = stringResource(R.string.select_date_grouping_dialog_title),
                    selectedConverter = prefMultipleDayConverter,
                    onConverterSelected = { preferencesViewModel.setMultipleDayConverterPreference(it); showMultipleDaysConverterDialog = false },
                    onDismiss = { showMultipleDaysConverterDialog = false }
                )
            }


            /*------------------------------------------------
            |                  Main Screen                   |
            ------------------------------------------------*/

            //-----------------   Header   -----------------//

            Icon(
                modifier = Modifier.size(140.dp),
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Text(preferencesViewModel.currentUser, style = MaterialTheme.typography.h6)

            Divider(Modifier.padding(top = 40.dp, bottom = 16.dp))


            //------------   Language Section   ------------//

            ListItem(
                icon = { Icon(Icons.Filled.Language, null, Modifier.padding(top = 7.dp)) },
                secondaryText = { Text(text = prefLanguage.language) },
                modifier = Modifier.clickable { showSelectLangDialog = true }
            ) {
                Text(text = stringResource(R.string.app_lang_setting_title))
            }


            //-------   TemporalConverter Section   --------//

            FormSubsection(title = stringResource(R.string.date_format_setting_section_title),
                Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp))

            // Today's Visits' date grouping and format
            ListItem(
                icon = { Icon(Icons.Filled.Today, null, Modifier.padding(top = 7.dp)) },
                secondaryText = { Text(text = TemporalConverter.valueOf(prefOneDayConverter).configName(LocalContext.current)) },
                modifier = Modifier.clickable { showDayConverterDialog = true }
            ) {
                Text(text = stringResource(R.string.today_visits_setting_title))
            }

            // Other Visits Screens' date grouping and format
            ListItem(
                icon = { Icon(Icons.Filled.CalendarMonth, null, Modifier.padding(top = 7.dp)) },
                secondaryText = { Text(text = TemporalConverter.valueOf(prefMultipleDayConverter).configName(LocalContext.current)) },
                modifier = Modifier.clickable { showMultipleDaysConverterDialog = true }
            ) {
                Text(text = stringResource(R.string.other_visits_setting_title))
            }

            Divider(Modifier.padding(top = 16.dp, bottom = 16.dp))


            //------   Save Today's Visits Section   -------//

            SaveAsJSONSection(visitsViewModel)
        }

    }
}


/*************************************************
 **        Save Today's Visits Section UI       **
 *************************************************/

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SaveAsJSONSection(visitsViewModel: VisitsViewModel) {

    /*------------------------------------------------
    |              Variables and States              |
    ------------------------------------------------*/
    val contentResolver = LocalContext.current.contentResolver
    val filename = stringResource(R.string.visit_json_name_template, LocalDate.now().format(DateTimeFormatter.ISO_DATE))


    /*------------------------------------------------
    |           Intent Launcher and Event            |
    ------------------------------------------------*/
    val saverLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument()) { uri ->
        if (uri != null) {
            try {
                contentResolver.openFileDescriptor(uri, "w")?.use {
                    FileOutputStream(it.fileDescriptor).use { fileOutputStream ->
                        fileOutputStream.write(
                            (visitsViewModel.todaysVisitsJson()).toByteArray()
                        )
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    val action = { saverLauncher.launch(filename) }


    /*------------------------------------------------
    |                 User Interface                 |
    ------------------------------------------------*/

    ListItem(
        Modifier.clickable(onClick = action),
        icon = { Icon(Icons.Filled.Save, stringResource(R.string.save_visits_button_description), Modifier.padding(top = 7.dp)) },
        secondaryText = { Text(text = filename) }
    ) {
        Text(text = stringResource(R.string.save_json_section_title), style = MaterialTheme.typography.subtitle1)
    }
}

