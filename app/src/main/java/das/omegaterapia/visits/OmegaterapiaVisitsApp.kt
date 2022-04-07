package das.omegaterapia.visits

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp


/*******************************************************************************
 ****                        Custom Aplication Class                        ****
 *******************************************************************************/

/*

This class is needed for Hilt Framework.

I also initialize notification channels here so they are not created each time we send a notification.

Avoiding code  repetition

*/

@HiltAndroidApp
class OmegaterapiaVisitsApp : Application() {
    override fun onCreate() {
        super.onCreate()

        /*------------------------------------------------
        |          Create Notification Channels          |
        ------------------------------------------------*/

        // Create the Authentication Notification Channel
        val name = getString(R.string.auth_channel_name)
        val descriptionText = getString(R.string.auth_channel_description)

        val mChannel = NotificationChannel("AUTH_CHANNEL", name, NotificationManager.IMPORTANCE_LOW)
        mChannel.description = descriptionText

        // Register the channel with the system
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}


/*******************************************************************************
 ****                    Enum Class for Notification IDs                    ****
 *******************************************************************************/

/*
Class used to centralize and have better control over application's notification IDs.
It uses an enum class what gives better readability over the code, and avoids ID mistakes

(In this app we only had one notification, but it's a good practice and eases future expansions and technical debt)
*/

enum class NotificationID(val id: Int) {
    USER_CREATED(0)
}