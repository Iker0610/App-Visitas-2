package das.omegaterapia.visits

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp


/*******************************************************************************
 ****                        Custom Application Class                       ****
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
        val authChannelName = getString(R.string.auth_channel_name)
        val authChannelDescription = getString(R.string.auth_channel_description)

        val authChannel = NotificationChannel(NotificationChannelID.AUTH_CHANNEL.name, authChannelName, NotificationManager.IMPORTANCE_LOW)
        authChannel.description = authChannelDescription

        // Create the Corporation Notification Channel
        val corporationChannelName = getString(R.string.corporation_channel_name)
        val corporationChannelDescription = getString(R.string.corporation_channel_description)

        val corporationChannel =
            NotificationChannel(NotificationChannelID.CORPORATION_CHANNEL.name, corporationChannelName, NotificationManager.IMPORTANCE_HIGH)
        corporationChannel.description = corporationChannelDescription


        // Create the Authentication Notification Channel
        val visitAlarmChannelName = getString(R.string.visit_alarm_channel_name)
        val visitAlarmChannelDescription = getString(R.string.visit_alarm_channel_description)

        val visitAlarmChannelChannel =
            NotificationChannel(NotificationChannelID.VISIT_ALARM_CHANNEL.name, visitAlarmChannelName, NotificationManager.IMPORTANCE_HIGH)
        visitAlarmChannelChannel.description = visitAlarmChannelDescription


        // Get notification manager
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification group
        val workRelatedChannelGroupName = getString(R.string.work_chennel_group_name)
        val workRelatedChannelGroup =
            NotificationChannelGroup(NotificationChannelID.WORK_RELATED_NOTIFICATION_GROUP.name, workRelatedChannelGroupName)

        notificationManager.createNotificationChannelGroup(workRelatedChannelGroup)

        // Add channels to group
        corporationChannel.group = NotificationChannelID.WORK_RELATED_NOTIFICATION_GROUP.name
        visitAlarmChannelChannel.group = NotificationChannelID.WORK_RELATED_NOTIFICATION_GROUP.name


        // Register the channels with the system
        notificationManager.createNotificationChannel(authChannel)
        notificationManager.createNotificationChannel(corporationChannel)
        notificationManager.createNotificationChannel(visitAlarmChannelChannel)
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

enum class NotificationChannelID {
    AUTH_CHANNEL,
    CORPORATION_CHANNEL,
    VISIT_ALARM_CHANNEL,
    WORK_RELATED_NOTIFICATION_GROUP
}

enum class NotificationID(val id: Int) {
    USER_CREATED(0),
    CORPORATION_NOTIFICATION(1),
    VISIT_ALARM(2)
}