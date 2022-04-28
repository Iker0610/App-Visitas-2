package das.omegaterapia.visits.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat.setTint
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import java.io.IOException


/**
 * Given a [geocoder] get given [address]'s latitude and longitude. It returns null if no match was found for that [address]-
 */
fun getLatLngFromAddress(geocoder: Geocoder, address: String): LatLng? {
    try {
        val location = geocoder.getFromLocationName(address, 1).getOrNull(0)
        if (location != null) return LatLng(location.latitude, location.longitude)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}


/**
 * Given a [vectorResId] get a [BitmapDescriptor] that can be used as a Google Map's Marker Icon.
 *
 * @param alpha: int between 0 and 255 indicating the opacity of the icon: 0 transparent and 255 fully opaque.
 * @param color: ARGB color in order to tint the loaded icon. If null original drawable colors will be used.
 */
fun bitmapDescriptorFromVector(
    context: Context,
    @DrawableRes vectorResId: Int,
    size: Int? = null,
    alpha: Int = 255,
    color: Int? = null,
): BitmapDescriptor {

    // Load drawable and apply options: alpha, color and size
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!.also {
        if (color != null) setTint(it, color)
        it.alpha = alpha

        it.setBounds(0, 0, size ?: it.intrinsicWidth, size ?: it.intrinsicHeight)
    }

    // Convert to bitmap
    val bitmap = Bitmap.createBitmap(size ?: vectorDrawable.intrinsicWidth, size ?: vectorDrawable.intrinsicHeight, Bitmap.Config.RGBA_F16)
    vectorDrawable.draw(Canvas(bitmap))

    // Convert to BitmapDescriptor and return
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}