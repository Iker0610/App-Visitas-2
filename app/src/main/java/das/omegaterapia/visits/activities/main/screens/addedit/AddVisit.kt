package das.omegaterapia.visits.activities.main.screens.addedit

import androidx.activity.compose.BackHandler
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import das.omegaterapia.visits.activities.main.composables.form.VisitForm
import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.ui.components.navigation.BackArrowTopBar


/**
 * Visit card creation screen.
 * It shows a [VisitForm] with a Top App Bar that has a back-arrow button and a [title] to exit the screen.
 *
 * Takes an [onBackPressed] callback to customize navigation.
 * Takes an [addVisitCard] callback to submit the new [VisitCard] and a [onSuccessfulSubmit] to call when the submission is successful.
 * [onSuccessfulSubmit] defaults to [onBackPressed].
 */
@Composable
fun AddVisitScreen(
    addVisitCard: suspend (VisitCard) -> Boolean,
    title: String,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    onSuccessfulSubmit: () -> Unit = onBackPressed,
) {
    // Overwrite the devices back button pressed action
    BackHandler(onBack = onBackPressed)

    /*------------------------------------------------
    |                 User Interface                 |
    ------------------------------------------------*/
    Scaffold(
        topBar = { BackArrowTopBar(title, onBackPressed) }
    ) {
        VisitForm(
            modifier = modifier,
            submitVisitCard = addVisitCard,
            onSuccessfulSubmit = onSuccessfulSubmit
        )
    }
}