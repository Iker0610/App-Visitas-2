package das.omegaterapia.visits.activities.main.screens.visitlists

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import das.omegaterapia.visits.R
import das.omegaterapia.visits.activities.main.VisitsViewModel
import das.omegaterapia.visits.activities.main.composables.VisitList
import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.model.entities.VisitId
import das.omegaterapia.visits.ui.theme.getButtonShape


/**
 * Screen that displays all the current user's visit cards that take place TODAY.
 * This screens uses a [VisitList] and passes to it the right parameters.
 *
 * It requires a [VisitsViewModel] to fetch the [VisitCard] list.
 *
 * As this is the main screen, it shows a dialog when the user tries to go back,
 * asking if he want's to close de app or not.
 *
 * The screen hoists most of its parameters to be personalized in higher layers.
 *
 * @param onItemEdit Callback for editing an item.
 * @param onItemDelete Callback for deleting an item.
 * @param onScrollStateChange Callback that must take a boolean indicating whether theres scrolling or not.
 * @param paddingAtBottom Whether to add a padding at the bottom of the list or not.
 */
@Composable
fun TodaysVisitsScreen(
    visitViewModel: VisitsViewModel,
    modifier: Modifier = Modifier,
    onItemEdit: (VisitCard) -> Unit = visitViewModel::currentToEditVisit::set,
    onItemDelete: (VisitId) -> Unit = { visitViewModel.deleteVisitCard(it) },
    lazyListState: LazyListState = rememberLazyListState(),
    onScrollStateChange: (Boolean) -> Unit = {},
    paddingAtBottom: Boolean = false,
) {
    /*************************************************
     **             Variables and States            **
     *************************************************/

    //-----------   Utility variables   ------------//
    val context = LocalContext.current


    //----------   ViewModel and State   -----------//
    val groupedVisits by visitViewModel.todaysVisits.collectAsState(emptyMap())
    var showExitAlertDialog by rememberSaveable { mutableStateOf(false) }


    /*************************************************
     **                Event Handlers               **
     *************************************************/

    BackHandler { showExitAlertDialog = true }


    /*************************************************
     **                User Interface               **
     *************************************************/

    /*------------------------------------------------
    |                    Dialogs                     |
    ------------------------------------------------*/
    if (showExitAlertDialog) {
        AlertDialog(
            title = { Text(stringResource(R.string.app_exit_dialog_text)) },
            confirmButton = {
                TextButton(onClick = { (context as Activity).finish() }, shape = getButtonShape()) {
                    Text(text = stringResource(id = R.string.exit_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitAlertDialog = false }, shape = getButtonShape()) {
                    Text(text = stringResource(id = R.string.cancel_button))
                }
            },
            onDismissRequest = { showExitAlertDialog = false },
            shape = RectangleShape,
        )
    }

    /*------------------------------------------------
    |                  Main Screen                   |
    ------------------------------------------------*/
    VisitList(
        groupedVisitCards = groupedVisits,
        modifier = modifier.fillMaxSize(),

        onItemEdit = onItemEdit,
        onItemDelete = onItemDelete,

        lazyListState = lazyListState,
        onScrollStateChange = onScrollStateChange,

        paddingAtBottom = paddingAtBottom
    )
}