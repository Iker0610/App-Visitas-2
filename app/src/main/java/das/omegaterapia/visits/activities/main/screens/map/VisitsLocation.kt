package das.omegaterapia.visits.activities.main.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
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
import kotlinx.coroutines.tasks.await
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

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun VisitsMap(
    locations: List<VisitInMap>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var geolocationInitialized by rememberSaveable { mutableStateOf(false) }
    var locationInitialized by rememberSaveable { mutableStateOf(false) }

    val listReadyState by derivedStateOf { locations.isNotEmpty() }
    var previousListReadyState by rememberSaveable { mutableStateOf(listReadyState) }

    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val showCurrentLocation by derivedStateOf { locationPermissionState.status.isGranted }

    var initialLocation: LatLng? by remember { mutableStateOf(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation ?: LatLng(43.264006875065775, -2.9351156221491275), 7f)
    }

    LaunchedEffect(listReadyState, initialLocation) {
        previousListReadyState = listReadyState

        if (locationInitialized || initialLocation != null) return@LaunchedEffect

        Log.i("Location", "Applying location obtained from visit.")

        if (listReadyState && !previousListReadyState) {
            (locations.getOrNull(locations.indexOfFirst { 0 <= it.timeDifference }) ?: locations.getOrNull(0))?.let {
                initialLocation = it.location
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it.location, 7f)

                Log.i("Location", "Location obtained from visit applied.")
            }
            locationInitialized = true
        }
    }


    LaunchedEffect(true) {
        if (geolocationInitialized) return@LaunchedEffect

        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        } else {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.await()?.let { location ->
                LatLng(location.latitude, location.longitude).let { locationLatLng ->
                    Log.i("Location", "Geolocation obtained.")

                    cameraPositionState.position = CameraPosition.fromLatLngZoom(locationLatLng, 7f)
                    initialLocation = locationLatLng

                    Log.i("Location", "Geolocation applied.")
                }
            }
        }
        geolocationInitialized = true
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
    val timeDifference: Long,
) {
    constructor(location: LatLng, visit: VisitCard) : this(
        location = location,
        clientName = visit.client.toString(),
        hour = visit.visitDate.format(DateTimeFormatter.ofPattern("HH:mm")),
        address = visit.client.direction.address,
        hasPassed = visit.visitDate < ZonedDateTime.now(),
        timeDifference = visit.visitDate.toEpochSecond() - ZonedDateTime.now().toEpochSecond()
    )
}