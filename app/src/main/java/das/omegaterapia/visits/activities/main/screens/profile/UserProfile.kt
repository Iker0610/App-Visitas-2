package das.omegaterapia.visits.activities.main.screens.profile

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider.getUriForFile
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
import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
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
@OptIn(ExperimentalMaterialApi::class, DelicateCoroutinesApi::class)
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
    val profilePicture: Bitmap? = preferencesViewModel.profilePicture

    //---------   Dialog Control States   ----------//
    var showSelectLangDialog by rememberSaveable { mutableStateOf(false) }
    var showDayConverterDialog by rememberSaveable { mutableStateOf(false) }
    var showMultipleDaysConverterDialog by rememberSaveable { mutableStateOf(false) }


    /*************************************************
     **                    Events                   **
     *************************************************/
    val toastMsg = stringResource(R.string.profile_not_taken_toast_msg)

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { pictureTaken ->
        if (pictureTaken) preferencesViewModel.setProfileImage()
        else Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show()
    }

    fun onEditImageRequest() {
        val profileImageDir = File(context.cacheDir, "images/profile/")
        Files.createDirectories(profileImageDir.toPath())

        val newProfileImagePath = File.createTempFile(preferencesViewModel.currentUser, ".png", profileImageDir)
        val contentUri: Uri = getUriForFile(context, "das.omegaterapia.visits.fileprovider", newProfileImagePath)
        preferencesViewModel.profilePicturePath = newProfileImagePath.path

        imagePickerLauncher.launch(contentUri)
    }


    /*************************************************
     **                User Interface               **
     *************************************************/

    Scaffold(topBar = { BackArrowTopBar(title, onBackPressed) }) { padding ->
        CenteredColumn(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
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
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(Modifier.padding(16.dp)) {
                    if (profilePicture == null) {
                        LoadingImagePlaceholder(size = 120.dp)
                    } else {
                        Image(
                            bitmap = profilePicture.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(bottom = 16.dp, end = 8.dp)
                        .clip(CircleShape)
                        .clickable(onClick = ::onEditImageRequest)
                ) {

                    Icon(Icons.Filled.Circle, contentDescription = null, Modifier.size(34.dp), tint = MaterialTheme.colors.primary)
                    Icon(Icons.Filled.Edit, contentDescription = null, Modifier.size(18.dp), tint = MaterialTheme.colors.surface)
                }

            }

            Text(preferencesViewModel.currentUser, style = MaterialTheme.typography.h6)

            Divider(Modifier.padding(top = 32.dp, bottom = 16.dp))


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


/*************************************************
 **          Image Loading Placeholder          **
 *************************************************/

@Composable
private fun LoadingImagePlaceholder(size: Dp = 140.dp) {
    // Creates an `InfiniteTransition` that runs infinite child animation values.
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        // `infiniteRepeatable` repeats the specified duration-based `AnimationSpec` infinitely.
        animationSpec = infiniteRepeatable(
            // The `keyframes` animates the value by specifying multiple timestamps.
            animation = keyframes {
                // One iteration is 1000 milliseconds.
                durationMillis = 1000
                // 0.7f at the middle of an iteration.
                0.7f at 500
            },
            // When the value finishes animating from 0f to 1f, it repeats by reversing the
            // animation direction.
            repeatMode = RepeatMode.Reverse
        )
    )

    Image(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .alpha(alpha),
        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}