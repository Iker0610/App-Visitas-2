package das.omegaterapia.visits.ui.components.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import das.omegaterapia.visits.R
import das.omegaterapia.visits.activities.main.screens.MainActivityScreens


/*************************************************
 **               Style Parameters              **
 *************************************************/

private val FABShape = CutCornerShape(50)


/*************************************************
 **                     FAB                     **
 *************************************************/

/**
 * Pre styled FAB button for main screen.
 *
 * @param onAdd Callback invoked when FAB button is pressed.
 */
@Composable
fun AddFloatingActionButton(
    onAdd: () -> Unit = {},
) {
    FloatingActionButton(onClick = onAdd, shape = FABShape) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.fab_icon_description)
            )
        }
    }
}


/*************************************************
 **                Bottom App Bar               **
 *************************************************/


/**
 * Custom styled [BottomAppBar] for the main screen.
 *
 * It has 2 sides:
 * - Left side: Navigation side. It has a menu icon and the current screen's name.
 * - Right side: Account side. It has a button to navigate to the Account Settings.
 *
 * @param currentScreenTitle
 * @param onOpenMenu
 * @param onAccountClicked
 */
@Composable
fun BottomNavBar(currentScreenTitle: String, onOpenMenu: () -> Unit, onAccountClicked: () -> Unit) {

    BottomAppBar(cutoutShape = FABShape) {

        //---------------   Left Side   ----------------//

        // We define a surface that fills the whole left half of the Bottom App bar.
        // We cut it to match the FAB cut shape
        Surface(
            shape = CutCornerShape(topEndPercent = 80), // Cut top right corner
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth(0.5f) // fill half the available space
        ) {
            // Make the row fill the surface and clickable
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onOpenMenu)) {
                // Add high alpha for the navigation section
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.navigation_menu_button_description))
                    }
                    Text(text = currentScreenTitle, style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold))
                }
            }
        }


        //---------------   Right Side   ---------------//

        Row {
            // Add an spacer to fill the remaining space
            Spacer(Modifier.weight(1f, true))

            // Add Account icon
            IconButton(onClick = onAccountClicked) {
                Icon(
                    MainActivityScreens.Account.icon,
                    contentDescription = MainActivityScreens.Account.title(LocalContext.current)
                )
            }
        }
    }
}


/*************************************************
 **           Navigation Drawer Header          **
 *************************************************/

/**
 * Custom header for the apps Bottom Navigation Drawer that has the logo, username of the current user and a close button.
 *
 * @param currentUser Current user's username.
 * @param onClose CloseButton's clicked event's callback.
 */
@Composable
fun NavDrawerHeader(
    currentUser: String,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //------------------   Logo   ------------------//
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary),
            modifier = Modifier
                .height(50.dp)
                .padding(end = 8.dp)
        )

        //--------   Current User's Username   ---------//
        Text(currentUser, style = MaterialTheme.typography.h6)

        // Spacer to fill the middle and push the button to the right
        Spacer(Modifier.weight(1f, true))

        //----------   Close Drawer Button   -----------//
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.close_navigation_drawer_button_description))
        }
    }
}


/*************************************************
 **         Top App Bar with Back-Arrow         **
 *************************************************/

/**
 * Pre styled [TopAppBar] with a back-arrow icon button and a title.
 *
 * @param title Current screen's title
 * @param onBackPressed On Back-Arrow button clicked event invoked callback.
 */
@Composable
fun BackArrowTopBar(
    title: String,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.top_app_bar_back_button_description))
            }
        }
    )
}