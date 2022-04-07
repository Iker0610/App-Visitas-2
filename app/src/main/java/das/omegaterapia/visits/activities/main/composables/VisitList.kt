package das.omegaterapia.visits.activities.main.composables

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import das.omegaterapia.visits.R
import das.omegaterapia.visits.data.visitList
import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.model.entities.VisitId
import das.omegaterapia.visits.ui.theme.OmegaterapiaTheme
import das.omegaterapia.visits.ui.theme.getButtonShape


/*******************************************************************************
 ****                           Visits List Screen                          ****
 *******************************************************************************/

/**
 * Given a grouped list of [VisitCard]s displays them in a lazy list-
 *
 * @param groupedVisitCards: map having lists of [VisitCard], the key will be used as header.
 * @param onItemEdit: action to take if one [SwipeableVisitCardItem]'s edit button is clicked.
 * @param onItemDelete: action to take if one [SwipeableVisitCardItem]'s delete button is clicked.
 * @param onScrollStateChange: callback to invoke when scroll state changes.
 * @param paddingAtBottom: if padding at the end must be added so the bottom app bar doesn't cover last item.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VisitList(
    groupedVisitCards: Map<String, List<VisitCard>>,
    modifier: Modifier = Modifier,
    onItemEdit: (VisitCard) -> Unit = {},
    onItemDelete: (VisitId) -> Unit = {},
    lazyListState: LazyListState = rememberLazyListState(),
    onScrollStateChange: (Boolean) -> Unit = {},
    paddingAtBottom: Boolean = false,
) {

    /*************************************************
     **             Variables and States            **
     *************************************************/

    val paddingAtBottomValue = if (paddingAtBottom) 90.dp else 16.dp

    /*
     * expandedVisitCardId controls which is the current expanded item; if null none is expanded
     *
     * swipedVisitCardId controls the item that has swipe "permissions",
     * this doesn't mean the item with permissions is currently swiped (that state is saved inside SwipeableVisitCardItem.
     * If null, all items has swipe "permissions"
     */
    val (expandedVisitCardId, setExpandedVisitCardId) = rememberSaveable { mutableStateOf<String?>(null) }
    val (swipedVisitCardId, setSwipedVisitCardId) = rememberSaveable { mutableStateOf<String?>(null) }


    /*------------------------------------------------
    |              Delete Item Section               |
    ------------------------------------------------*/

    var toDeleteItemId by rememberSaveable { mutableStateOf<String?>(null) }


    //-----------   Delete Item Dialog   -----------//

    if (toDeleteItemId != null) {
        val dismissCallback = { toDeleteItemId = null }
        AlertDialog(
            title = { Text(text = stringResource(R.string.delete_visit_card_dialog_title)) },
            confirmButton = {
                TextButton(onClick = { onItemDelete(VisitId(toDeleteItemId!!)); toDeleteItemId = null }, shape = getButtonShape()) {
                    Text(text = stringResource(R.string.delete_button))
                }
            },
            dismissButton = {
                TextButton(onClick = dismissCallback, shape = getButtonShape()) {
                    Text(text = stringResource(R.string.dismiss_button))
                }
            },
            onDismissRequest = dismissCallback,
            shape = RectangleShape,
        )
    }


    /*************************************************
     **                User Interface               **
     *************************************************/

    LazyColumn(
        modifier = modifier.clipToBounds(), // Important so the swiped card doesn't clip on Navigation Rail
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = lazyListState,
        contentPadding = PaddingValues(bottom = paddingAtBottomValue)
    ) {
        // Iterate over map
        groupedVisitCards.forEach { (groupTitle, groupVisitCards) ->

            //-----------   Sub-Section Header   -----------//

            stickyHeader { VisitGroupHeader(groupTitle) }


            //----   Sub-Section Items (Visit Cards)   -----//

            items(groupVisitCards, key = { visitCard -> visitCard.hashCode() })
            { visitCard ->
                SwipeableVisitCardItem(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    visitCard = visitCard,
                    isExpanded = visitCard.id == expandedVisitCardId,
                    canBeSwipedToSide = visitCard.id == swipedVisitCardId || swipedVisitCardId == null,

                    onEdit = onItemEdit,
                    onDelete = { toDeleteItemId = visitCard.id },


                    /*
                     * If another item is already extended or swiped unextend/unswipe them
                     * by setting the current item as the expanded one and the only one with extend permissions
                     *
                     * If this is the already expanded item unexpand it
                     */
                    onClick = {
                        if (expandedVisitCardId != it.id) {
                            setExpandedVisitCardId(it.id)
                            setSwipedVisitCardId(it.id)
                        } else {
                            setExpandedVisitCardId(null)
                        }
                    },

                    /*
                     * if this isn't the current swiped item then set this as the current one with swipe permissions
                     * also unexpand all items
                     */
                    onSwipe = {
                        if (swipedVisitCardId != it.id) {
                            setSwipedVisitCardId(it.id)
                            setExpandedVisitCardId(null)
                        }
                    }
                )
            }
        }
    }

    // Event called each time the scroll state changes
    LaunchedEffect(lazyListState.isScrollInProgress) {
        onScrollStateChange(lazyListState.isScrollInProgress)
    }
}

/**
 * Sub-section sticky header composable. It displays the given [groupTitle] representing that group of items.
 */
@Composable
fun VisitGroupHeader(groupTitle: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colors.primary.copy(alpha = 1f).compositeOver(MaterialTheme.colors.surface),
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 8.dp
    ) {
        Text(
            text = groupTitle,
            style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

//------------------------------------------------------------------------------------------------------------------

@Preview(showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun VisitListPreview() {
    val groupedVisits = mapOf(
        "Lunes" to visitList.subList(0, 5),
        "Martes" to visitList.subList(5, 12),
        "Miércoles" to emptyList(),
        "Jueves" to visitList.subList(12, 20),
        "Viernes" to visitList.subList(20, visitList.size),
    )
    OmegaterapiaTheme {
        Surface {
            VisitList(groupedVisitCards = groupedVisits)
        }
    }
}
