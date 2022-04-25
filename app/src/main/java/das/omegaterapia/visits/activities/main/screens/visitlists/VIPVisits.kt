package das.omegaterapia.visits.activities.main.screens.visitlists

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import das.omegaterapia.visits.activities.main.VisitsViewModel
import das.omegaterapia.visits.activities.main.composables.VisitList
import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.model.entities.VisitId
import das.omegaterapia.visits.services.RemainderStatus


/**
 * Screen that displays the current user's visit cards that are with a VIP Client.
 * This screens uses a [VisitList] and passes to it the right parameters.
 *
 * It requires a [VisitsViewModel] to fetch the [VisitCard] list.
 *
 * The screen hoists most of its parameters to be personalized in higher layers.
 *
 * @param onItemEdit Callback for editing an item.
 * @param onItemDelete Callback for deleting an item.
 * @param onScrollStateChange Callback that must take a boolean indicating whether theres scrolling or not.
 * @param paddingAtBottom Whether to add a padding at the bottom of the list or not.
 */
@Composable
fun VIPVisitsScreen(
    visitViewModel: VisitsViewModel,
    modifier: Modifier = Modifier,
    onItemEdit: (VisitCard) -> Unit,
    onItemDelete: (VisitId) -> Unit,
    onItemRemainder: (VisitCard, RemainderStatus) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
    onScrollStateChange: (Boolean) -> Unit = {},
    paddingAtBottom: Boolean = false,
) {
    // Get the list to be shown
    val groupedVisits by visitViewModel.groupedVipVisits.collectAsState(emptyMap())
    val visitRemainderStatus by visitViewModel.visitsRemainderStatuses.collectAsState(emptyMap())

    /*------------------------------------------------
    |                 User Interface                 |
    ------------------------------------------------*/
    VisitList(
        groupedVisitCards = groupedVisits,
        visitsRemainderStatuses = visitRemainderStatus,
        modifier = modifier.fillMaxSize(),

        onItemEdit = onItemEdit,
        onItemDelete = onItemDelete,
        onItemRemainder = onItemRemainder,

        lazyListState = lazyListState,
        onScrollStateChange = onScrollStateChange,

        paddingAtBottom = paddingAtBottom
    )
}