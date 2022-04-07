package das.omegaterapia.visits.ui.components.generic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Chip
import androidx.compose.material.ChipColors
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import das.omegaterapia.visits.ui.theme.getButtonShape


/*******************************************************************************
 ****                                 Chips                                 ****
 *******************************************************************************/


/*************************************************
 **               Style Parameters              **
 *************************************************/

/**
 * These parameters are adjusted versions of Compose's Material [ChipDefaults].chipColors()
 * in order to create an outlined version with secondary color as selected color.
 */

private const val SurfaceOverlayOpacity = 0.12f


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun selectedOutlinedChoiceChipColors(
    backgroundColor: Color = MaterialTheme.colors.secondary.copy(alpha = SurfaceOverlayOpacity).compositeOver(MaterialTheme.colors.surface),
    contentColor: Color = MaterialTheme.colors.secondary,
    leadingIconContentColor: Color = contentColor.copy(alpha = 0.85f),

    disabledBackgroundColor: Color =
        MaterialTheme.colors.onSurface
            .copy(alpha = ContentAlpha.disabled * SurfaceOverlayOpacity)
            .compositeOver(MaterialTheme.colors.surface),
    disabledContentColor: Color = contentColor.copy(alpha = ContentAlpha.disabled * ChipDefaults.ContentOpacity),
    disabledLeadingIconContentColor: Color = leadingIconContentColor.copy(alpha = ContentAlpha.disabled * ChipDefaults.LeadingIconOpacity),
): ChipColors = ChipDefaults.chipColors(
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    leadingIconContentColor = leadingIconContentColor,
    disabledBackgroundColor = disabledBackgroundColor,
    disabledContentColor = disabledContentColor,
    disabledLeadingIconContentColor = disabledLeadingIconContentColor
)


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun unselectedOutlinedChoiceChipColors(
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = MaterialTheme.colors.onSurface,
    leadingIconContentColor: Color = contentColor.copy(alpha = ChipDefaults.LeadingIconOpacity),

    disabledBackgroundColor: Color =
        MaterialTheme.colors.onSurface
            .copy(alpha = ContentAlpha.disabled * SurfaceOverlayOpacity)
            .compositeOver(MaterialTheme.colors.surface),
    disabledContentColor: Color = contentColor.copy(alpha = ContentAlpha.disabled * ChipDefaults.ContentOpacity),
    disabledLeadingIconContentColor: Color = leadingIconContentColor.copy(alpha = ContentAlpha.disabled * ChipDefaults.LeadingIconOpacity),
): ChipColors = ChipDefaults.chipColors(
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    leadingIconContentColor = leadingIconContentColor,
    disabledBackgroundColor = disabledBackgroundColor,
    disabledContentColor = disabledContentColor,
    disabledLeadingIconContentColor = disabledLeadingIconContentColor
)


/*************************************************
 **               Chip Composable               **
 *************************************************/

/**
 * Composable that adds custom color and border to Compose's Material's [Chip].
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OutlinedChoiceChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = getButtonShape(),
    colors: ChipColors = if (selected) selectedOutlinedChoiceChipColors() else unselectedOutlinedChoiceChipColors(),
    leadingIcon: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Chip(
        onClick = onClick,
        modifier = modifier,
        enabled = true,
        interactionSource = interactionSource,
        shape = shape,
        border = BorderStroke(1.dp, color = colors.contentColor(enabled = true).value.copy(alpha = 0.36f)),
        colors = colors,
        leadingIcon = leadingIcon,
        content = content
    )
}