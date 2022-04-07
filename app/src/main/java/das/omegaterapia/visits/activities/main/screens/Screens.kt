package das.omegaterapia.visits.activities.main.screens

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RecentActors
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.ui.graphics.vector.ImageVector


/**
 * Enum class that defines the different routes of the apk.
 *
 * @property route route for the navigation graph.
 * @property icon unique icon to be displayed that represents the route.
 */
enum class MainActivityScreens(var route: String, var icon: ImageVector) {
    TodaysVisits("todays_visits", Icons.Filled.Today),
    AllVisits("all_visits", Icons.Filled.RecentActors),
    VIPs("vip_visits", Icons.Filled.Star),
    AddVisit("add_visit", Icons.Filled.Add),
    EditVisit("edit_visit", Icons.Filled.Edit),
    Account("account", Icons.Filled.AccountCircle);

    // Get if this MainActivityScreen is one of the main screens
    fun isNavigable(): Boolean = this in navigableScreens

    // Get the title of the screen from R.strings
    fun title(context: Context): String {
        val titleStringID = context.resources.getIdentifier(this.route + "_screen_title", "string", context.packageName)
        return context.getString(titleStringID)
    }

    // Utility variables and methods
    companion object {

        // List of screens that must appear in navigation rail / navigation drawer etc...
        val navigableScreens = setOf(TodaysVisits, AllVisits, VIPs)

        // Given a route get the corresponding MainActivityScreen
        // Original code from Google's Compose Navigation Codelab
        fun fromRoute(route: String?): MainActivityScreens =
            when (route?.substringBefore("/")) {
                TodaysVisits.route -> TodaysVisits
                AllVisits.route -> AllVisits
                VIPs.route -> VIPs
                AddVisit.route -> AddVisit
                EditVisit.route -> EditVisit
                Account.route -> Account
                null -> TodaysVisits
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }

        // Get if the given route is one of the main screens
        fun isNavigable(route: String?): Boolean = fromRoute(route).isNavigable()
    }
}