package das.omegaterapia.visits.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


/*************************************************
 **              Dark Color Palette             **
 *************************************************/
private val DarkColorPalette = darkColors(
    primary = BlueGrey200,
    primaryVariant = BlueGrey900,
    secondary = Orange300,
)

/*************************************************
 **             Light Color Palette             **
 *************************************************/
private val LightColorPalette = lightColors(
    primary = BlueGrey600,
    primaryVariant = BlueGrey900,
    secondary = Orange300

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)


/*******************************************************************************
 ****                                 Theme                                 ****
 *******************************************************************************/
@Composable
fun OmegaterapiaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    // Chose color palette based on system configuration
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    // Define the theme
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

// Custom shape for buttons
@Composable
fun getButtonShape() = CutCornerShape(topStartPercent = 25, bottomEndPercent = 25)