package das.omegaterapia.visits.activities.main.screens.map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import das.omegaterapia.visits.activities.main.VisitsViewModel
import das.omegaterapia.visits.ui.components.navigation.BackArrowTopBar

@Composable
fun VisitsMapScreen(
    title: String,
    visitsViewModel: VisitsViewModel,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
) {
    // Overwrite the devices back button pressed action
    BackHandler(onBack = onBackPressed)

    /*------------------------------------------------
    |                 User Interface                 |
    ------------------------------------------------*/
    Scaffold(
        modifier = modifier,
        topBar = { BackArrowTopBar(title, onBackPressed) }
    ) { paddingValues ->
        VisitsMap(
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
private fun VisitsMap(modifier: Modifier = Modifier) {
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            position = singapore,
            title = "Singapore",
            snippet = "Marker in Singapore"
        )
    }

}