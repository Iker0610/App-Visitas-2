package das.omegaterapia.visits.ui.components.generic

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


/**
 * Custom Composable that allows for a swipeable item that has 3 position (swiped right, not swiped, swiped left).
 * On swiped to a side it's possible to access to the [background] composable.
 *
 * It's inspired by Compose's [SwipeToDismiss] item but adapted with custom logic and anchors.
 *
 * @param state The swipe state. It can be hoisted in order to apply much complex custom logic on higher layers.
 * @param swipeEnabled If the swipe gesture is enabled.
 * @param swipeableContent Main content that will be swiped horizontally.
 * @param background Socket for background composable. It will be shown when the [swipeableContent] is swiped to a side.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableItem(
    modifier: Modifier = Modifier,
    state: SwipeableState<Int> = rememberSwipeableState(initialValue = 0),
    swipeEnabled: Boolean = true,
    background: @Composable RowScope.() -> Unit,
    swipeableContent: @Composable RowScope.() -> Unit,
) {
    val squareSize = 110.dp
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1, -sizePx to 2)

    Box(
        modifier = modifier
            .swipeable(
                state = state,
                anchors = anchors,
                orientation = Orientation.Horizontal,
                enabled = swipeEnabled
            ),
    ) {
        Row(
            content = background,
            modifier = Modifier.matchParentSize()
        )
        Row(
            content = swipeableContent,
            modifier = Modifier.offset { IntOffset(state.offset.value.roundToInt(), 0) }
        )
    }
}

