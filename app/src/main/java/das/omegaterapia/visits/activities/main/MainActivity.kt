package das.omegaterapia.visits.activities.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.NavigationRail
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import das.omegaterapia.visits.activities.main.screens.MainActivityScreens
import das.omegaterapia.visits.activities.main.screens.addedit.AddVisitScreen
import das.omegaterapia.visits.activities.main.screens.addedit.EditVisitScreen
import das.omegaterapia.visits.activities.main.screens.profile.PreferencesViewModel
import das.omegaterapia.visits.activities.main.screens.profile.UserProfileScreen
import das.omegaterapia.visits.activities.main.screens.visitlists.AllVisitsScreen
import das.omegaterapia.visits.activities.main.screens.visitlists.TodaysVisitsScreen
import das.omegaterapia.visits.activities.main.screens.visitlists.VIPVisitsScreen
import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.ui.components.generic.CenteredColumn
import das.omegaterapia.visits.ui.components.generic.DrawerButton
import das.omegaterapia.visits.ui.components.generic.NavRailIcon
import das.omegaterapia.visits.ui.components.navigation.AddFloatingActionButton
import das.omegaterapia.visits.ui.components.navigation.BottomNavBar
import das.omegaterapia.visits.ui.components.navigation.NavDrawerHeader
import das.omegaterapia.visits.ui.theme.OmegaterapiaTheme
import das.omegaterapia.visits.utils.WindowSize
import das.omegaterapia.visits.utils.WindowSizeFormat
import das.omegaterapia.visits.utils.rememberWindowSizeClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/*******************************************************************************
 ****                             Main Activity                             ****
 *******************************************************************************/

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /*************************************************
     **                  ViewModel                  **
     *************************************************/

    private val preferencesViewModel: PreferencesViewModel by viewModels()


    /*************************************************
     **          Activity Lifecycle Methods         **
     *************************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Reload lang if it changed but do nor recreate it
            preferencesViewModel.reloadLang(preferencesViewModel.prefLang.collectAsState(initial = preferencesViewModel.currentSetLang).value, this)

            // Set theme
            OmegaterapiaTheme {
                // Get window size
                val windowSize = rememberWindowSizeClass()

                // Load UI
                MainActivityScreen(preferencesViewModel = preferencesViewModel, windowSize = windowSize)
            }
        }
    }
}


/*******************************************************************************
 ****                              Main Screen                              ****
 *******************************************************************************/

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun MainActivityScreen(
    preferencesViewModel: PreferencesViewModel = hiltViewModel(),
    visitViewModel: VisitsViewModel = hiltViewModel(),
    windowSize: WindowSize,
) {
    /*************************************************
     **             Variables and States            **
     *************************************************/

    //-----------   Utility variables   ------------//

    val scope = rememberCoroutineScope()


    //-------------   Nav-Controller   -------------//

    val navController = rememberAnimatedNavController()
    val currentRoute by navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)


    //-----------   Navigation States   ------------//

    /*
     * We should ONLY show navigation elements on the 3 main screens ("navigable screens")
     *
     * If the WindowSize width's WindowSizeFormat is compact then we should show a bottom app bar
     * and enable the bottom navigation drawer (bottom menu).
     * If we are scrolling (up or down) we should hide these navigation elements so the user has better visibility.
     *
     *
     * Else if the WindowSizeFormat is not compact we should only allow a Navigation Rail and hide previous elements.
     *
     *
     * In conclusion:
     *
     * Width - Compact (<600) and NOT Scrolling -> Bottom app bar + Bottom navigation drawer (menu)
     * Width - NOT Compact (600<) -> Navigation Rail
     */

    val enableNavigation = MainActivityScreens.isNavigable(currentRoute?.destination?.route)
    val enableBottomNavigation = enableNavigation && windowSize.width == WindowSizeFormat.Compact
    val enableRailNavigation = enableNavigation && !enableBottomNavigation

    var isScrolling by rememberSaveable { mutableStateOf(false) }
    val showBottomNavigation = enableBottomNavigation && !isScrolling

    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val gesturesEnabled = drawerState.currentValue != BottomDrawerValue.Closed // Enable gestures only when menu is open

    val scaffoldState = rememberScaffoldState()


    /*************************************************
     **                    Events                   **
     *************************************************/

    // Close bottom navigation menu if it was open and we changed layout to the one with a navigation rail
    LaunchedEffect(enableBottomNavigation) {
        if (!enableBottomNavigation && drawerState.currentValue != BottomDrawerValue.Closed) {
            scope.launch { drawerState.close() }
        }
    }


    /*************************************************
     **            Common Event Callbacks           **
     *************************************************/

    // Pop current screen from backstack (if we didn't pop anything make sure to travel to main screen)
    val navigateBack: () -> Unit = {
        scope.launch(Dispatchers.Main) {
            if (!navController.popBackStack()) {
                navController.navigate(MainActivityScreens.TodaysVisits.route)
            }
        }
    }

    // Set the current "toEditVisit" in the view model and navigate to edit screen
    val onEditVisit: (VisitCard) -> Unit = {
        visitViewModel.currentToEditVisit = it
        navController.navigate(MainActivityScreens.EditVisit.route) { launchSingleTop = true }
    }

    // Navigate to the current user's account page. (Passing the current user as a parameter in the route)
    val onNavigateToAccount =
        { navController.navigate(MainActivityScreens.Account.route + "/${visitViewModel.currentUser}") { launchSingleTop = true } }


    /*************************************************
     **              Common UI Elements             **
     *************************************************/

    @Composable
    fun FAB() {
        AddFloatingActionButton(
            // Open Ass Visit Screen
            onAdd = { navController.navigate(MainActivityScreens.AddVisit.route) { launchSingleTop = true } },
        )
    }


    //----------------------------------------------//

    /*************************************************
     **                   Main UI                   **
     *************************************************/

    //----------------------------------------------//


    /*************************************************
     **    Bottom Navigation Drawer (Menu) Layout   **
     *************************************************/

    BottomDrawer(
        gesturesEnabled = gesturesEnabled,
        drawerState = drawerState,
        drawerShape = CutCornerShape(topStartPercent = 7, topEndPercent = 7),
        drawerContent = {

            /*------------------------------------------------
            |    Bottom Navigation Drawer (Menu) Content     |
            ------------------------------------------------*/

            LazyColumn(Modifier.padding(top = 16.dp, bottom = 22.dp)) {

                //------------   Nav Menu Header   -------------//

                item {
                    NavDrawerHeader(
                        currentUser = visitViewModel.currentUser,
                        onClose = { scope.launch { drawerState.close() } },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Divider(
                        Modifier
                            .padding(top = 8.dp, bottom = 8.dp)
                            .fillMaxWidth()
                    )
                }


                //--   Nav Menu Navigation Buttons (routes)   --//

                items(MainActivityScreens.navigableScreens.toList()) {
                    DrawerButton(
                        icon = it.icon,
                        label = it.title(LocalContext.current),
                        isSelected = currentRoute?.destination?.route == it.route,
                        action = {
                            // Close Drawer and then navigate to route
                            scope.launch {
                                drawerState.close()
                                navController.navigate(it.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    )
                }
            }
        },
    ) {

        /*------------------------------------------------
        |      Screen Content + Navigation Bar/Rail      |
        ------------------------------------------------*/

        Scaffold(
            scaffoldState = scaffoldState,

            //-----------   Bottom App Bar FAB   -----------//

            floatingActionButton = {
                AnimatedVisibility(
                    showBottomNavigation,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    FAB()
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            isFloatingActionButtonDocked = true,


            //-------------   Bottom App Bar   -------------//

            bottomBar = {
                AnimatedVisibility(
                    showBottomNavigation,
                    enter = slideInVertically(initialOffsetY = { 2 * it }),
                    exit = slideOutVertically(targetOffsetY = { 2 * it })
                ) {
                    BottomNavBar(
                        currentScreenTitle = MainActivityScreens.fromRoute(currentRoute?.destination?.route).title(LocalContext.current),
                        onOpenMenu = { scope.launch { drawerState.open() } },
                        onAccountClicked = onNavigateToAccount
                    )
                }
            },
        )
        {


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {


                /*------------------------------------------------
                |                Navigation Rail                 |
                ------------------------------------------------*/

                /*
                 * Specs:
                 *
                 * - Animated  visibility with slide In/Out animations (other content size must adjust progressively)
                 *
                 * - Buttons:
                 *      - FAB Button anchored on top
             *          - Account button anchored on bottom
                 *      - Main navigation buttons on column, centered in the remaining space between FAB and Account button
                 */

                AnimatedVisibility(
                    enableRailNavigation,
                    enter = slideInHorizontally(initialOffsetX = { -it }) + expandHorizontally(),
                    exit = slideOutHorizontally(targetOffsetX = { -it }) + shrinkHorizontally()
                ) {
                    NavigationRail(

                        //----------   Navigation Rail FAB   -----------//
                        header = { Box(Modifier.padding(8.dp)) { FAB() } }

                    ) {

                        //---------   Main Navigation Routes   ---------//

                        CenteredColumn(Modifier.weight(1f, true)) {
                            MainActivityScreens.navigableScreens.forEach { screen ->
                                // Google's recommended icons

//                                  NavigationRailItem(
//                                    icon = { Icon(screen.icon, contentDescription = screen.title(LocalContext.current)) },
//                                    label = null,
//                                    selected = currentRoute?.destination?.route == screen.route,
//                                    onClick = {
//                                        navController.navigate(screen.route) {
//                                            popUpTo(navController.graph.startDestinationId) {
//                                                saveState = true
//                                            }
//                                            launchSingleTop = true
//                                            restoreState = true
//                                        }
//                                    }
//                                )


                                // Personalized icons that indicates better the current route
                                NavRailIcon(
                                    icon = screen.icon,
                                    contentDescription = screen.title(LocalContext.current),
                                    isSelected = currentRoute?.destination?.route == screen.route,
                                    action = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }

                        //--------   Account Naviggation Icon   --------//

                        NavRailIcon(
                            icon = MainActivityScreens.Account.icon,
                            contentDescription = MainActivityScreens.Account.title(LocalContext.current),
                            isSelected = currentRoute?.destination?.route == MainActivityScreens.Account.route,
                            action = onNavigateToAccount
                        )
                    }
                }


                /*------------------------------------------------
                |                 Screen Content                 |
                ------------------------------------------------*/

                // Nav host
                AnimatedNavHost(
                    navController = navController,
                    startDestination = MainActivityScreens.TodaysVisits.route
                ) {

                    //---------   Today's Visits Screen   ----------//
                    composable(
                        route = MainActivityScreens.TodaysVisits.route,
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() },
                    ) {
                        TodaysVisitsScreen(
                            visitViewModel = visitViewModel,
                            onItemEdit = onEditVisit,
                            onScrollStateChange = { isScrolling = it },
                            paddingAtBottom = enableBottomNavigation
                        )
                    }


                    //-----------   All Visits Screen   ------------//
                    composable(
                        route = MainActivityScreens.AllVisits.route,
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() },
                    ) {
                        AllVisitsScreen(
                            visitViewModel = visitViewModel,
                            onItemEdit = onEditVisit,
                            onScrollStateChange = { isScrolling = it },
                            paddingAtBottom = enableBottomNavigation
                        )
                    }


                    //-----------   VIP Visits Screen   ------------//
                    composable(
                        route = MainActivityScreens.VIPs.route,
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() },
                    ) {
                        VIPVisitsScreen(
                            visitViewModel = visitViewModel,
                            onItemEdit = onEditVisit,
                            onScrollStateChange = { isScrolling = it },
                            paddingAtBottom = enableBottomNavigation
                        )
                    }


                    //-------------   Account Screen   -------------//
                    composable(
                        route = "${MainActivityScreens.Account.route}/{username}",
                        arguments = listOf(navArgument("username") { type = NavType.StringType }),
                        enterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = spring(
                                    dampingRatio = 2f,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        },
                        exitTransition = {
                            slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        },
                    ) {
                        UserProfileScreen(
                            MainActivityScreens.Account.title(LocalContext.current),
                            onBackPressed = navigateBack,
                            visitsViewModel = visitViewModel,
                            preferencesViewModel = preferencesViewModel
                        )
                    }


                    //------------   Add Visit Screen   ------------//
                    composable(
                        route = MainActivityScreens.AddVisit.route,
                        enterTransition = {
                            slideInVertically(
                                initialOffsetY = { 2 * it },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessVeryLow
                                )
                            )
                        },
                        exitTransition = {
                            slideOutVertically(
                                targetOffsetY = { 2 * it },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessVeryLow
                                )
                            )
                        },
                    ) {
                        AddVisitScreen(
                            title = MainActivityScreens.AddVisit.title(LocalContext.current),
                            addVisitCard = visitViewModel::addVisitCard,
                            onBackPressed = navigateBack
                        )
                    }


                    //-----------   Edit Visit Screen   ------------//
                    composable(
                        route = MainActivityScreens.EditVisit.route,
                        enterTransition = {
                            slideInVertically(
                                initialOffsetY = { 2 * it },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessVeryLow
                                )
                            )
                        },
                        exitTransition = {
                            slideOutVertically(
                                targetOffsetY = { 2 * it },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessVeryLow
                                )
                            )
                        },
                    ) {
                        /*
                         * Make sure that the currentToEditVisit is not null, else go back
                         */
                        if (visitViewModel.currentToEditVisit == null) {
                            if (currentRoute?.destination?.route == MainActivityScreens.EditVisit.route
                                && !navController.popBackStack()
                            ) {
                                navController.navigate(MainActivityScreens.TodaysVisits.route)
                            }
                        } else {
                            EditVisitScreen(
                                title = MainActivityScreens.EditVisit.title(LocalContext.current),
                                visitCard = visitViewModel.currentToEditVisit!!,
                                onEditVisitCard = visitViewModel::updateVisitCard,
                                onBackPressed = {
                                    visitViewModel.currentToEditVisit = null // Reset currentToEditVisit on exit
                                    navigateBack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }


    //------------   Debug Log Screen   ------------//
    LaunchedEffect(currentRoute) {
        Log.d("navigation",
            "Back stack changed ${currentRoute?.destination?.route} \n ${navController.backQueue.joinToString("  -  ") { it.destination.route ?: "root_path" }}")
    }
}


