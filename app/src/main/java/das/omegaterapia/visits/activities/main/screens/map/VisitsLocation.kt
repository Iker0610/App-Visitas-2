package das.omegaterapia.visits.activities.main.screens.map

import android.Manifest
import android.location.Geocoder
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import das.omegaterapia.visits.R
import das.omegaterapia.visits.activities.main.VisitsViewModel
import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.ui.components.navigation.BackArrowTopBar
import das.omegaterapia.visits.utils.bitmapDescriptorFromVector
import das.omegaterapia.visits.utils.getLatLngFromAddress
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun VisitsMapScreen(
    title: String,
    visitsViewModel: VisitsViewModel,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
) {
    // Overwrite the devices back button pressed action
    BackHandler(onBack = onBackPressed)

    val geocoder = Geocoder(LocalContext.current)

    val visits by visitsViewModel.todayVisits.collectAsState(initial = emptyList())
    val locations by remember {
        derivedStateOf {
            visits.mapNotNull { visit ->
                val location = getLatLngFromAddress(geocoder, visit.client.direction.toString())

                if (location != null) VisitInMap(location, visit)
                else null
            }
        }
    }

    /*------------------------------------------------
    |                 User Interface                 |
    ------------------------------------------------*/
    Scaffold(
        modifier = modifier,
        topBar = { BackArrowTopBar(title, onBackPressed) }
    ) { paddingValues ->
        VisitsMap(
            locations,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun VisitsMap(
    locations: List<VisitInMap>,
    modifier: Modifier = Modifier,
) {
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val showCurrentLocation by derivedStateOf { locationPermissionState.status.isGranted }

    LaunchedEffect(true) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    val bilbo = LatLng(43.264006875065775, -2.9351156221491275)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bilbo, 10f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = showCurrentLocation)
    ) {
        locations.forEach {
            Marker(
                position = it.location,
                title = "${it.hour} - ${it.clientName}",
                snippet = it.address,
                icon = bitmapDescriptorFromVector(
                    LocalContext.current,
                    R.drawable.ic_visit_location_bitonal,
                    size = 120,
                    alpha = if (it.hasPassed) 125 else 255
                )
            )
        }
    }
}

private data class VisitInMap(
    val location: LatLng,
    val clientName: String,
    val hour: String,
    val address: String,
    val hasPassed: Boolean,
) {
    constructor(location: LatLng, visit: VisitCard) : this(
        location = location,
        clientName = visit.client.toString(),
        hour = visit.visitDate.format(DateTimeFormatter.ofPattern("HH:mm")),
        address = visit.client.direction.address,
        hasPassed = visit.visitDate < ZonedDateTime.now()
    )
}