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


fun getLatLngFromAddress(geocoder: Geocoder, address: String): LatLng? {
    try {
        val location = geocoder.getFromLocationName(address, 1).getOrNull(0)
        if (location != null) {
            return LatLng(location.latitude, location.longitude)
        }

    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorResId: Int, color: Int? = null, size: Int? = null): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!.also { if (color != null) setTint(it, color) }
    vectorDrawable.setBounds(0, 0, size ?: vectorDrawable.intrinsicWidth, size ?: vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(size ?: vectorDrawable.intrinsicWidth, size ?: vectorDrawable.intrinsicHeight, Bitmap.Config.RGBA_F16)
    vectorDrawable.draw(Canvas(bitmap))
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}