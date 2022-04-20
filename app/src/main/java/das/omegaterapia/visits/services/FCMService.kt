package das.omegaterapia.visits.services

import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import das.omegaterapia.visits.NotificationChannelID
import das.omegaterapia.visits.NotificationID
import das.omegaterapia.visits.R

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {}

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { notification ->
            // TODO
            Log.d("FCM", "Message Notification Title: ${notification.title}")
            Log.d("FCM", "Message Notification Body: ${notification.body}")

            // Show user created notification
            val builder = NotificationCompat.Builder(this, NotificationChannelID.CORPORATION_CHANNEL.name)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)) {
                notify(NotificationID.CORPORATION_NOTIFICATION.id, builder.build())
            }
        }
    }
}