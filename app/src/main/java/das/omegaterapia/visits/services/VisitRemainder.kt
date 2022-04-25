package das.omegaterapia.visits.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import das.omegaterapia.visits.NotificationChannelID
import das.omegaterapia.visits.NotificationID
import das.omegaterapia.visits.R
import das.omegaterapia.visits.model.entities.CompactVisitData
import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.model.repositories.VisitRemainderRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


// TODO: Anotar
@AndroidEntryPoint
class VisitRemainder : BroadcastReceiver() {

    @Inject
    lateinit var visitRemainderRepository: VisitRemainderRepository


    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "android.intent.action.BOOT_COMPLETED" -> reloadAlarms(context)
            launchRemainderAction -> launchVisitRemainderNotification(context, intent.getParcelableExtra("data")!!)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun launchVisitRemainderNotification(context: Context, visit: CompactVisitData) {
        // Delete alarm from database
        GlobalScope.launch(Dispatchers.IO) { visitRemainderRepository.removeAlarm(visit.id) }

        // Show user created notification
        val builder = NotificationCompat.Builder(context, NotificationChannelID.AUTH_CHANNEL.name)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(context.getString(R.string.visit_remainder_notification_title, visit.hour))
            .setContentText(context.getString(R.string.visit_remainder_notification_text, visit.hour, visit.client))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NotificationID.VISIT_ALARM.id, builder.build())
        }
    }


    private fun reloadAlarms(context: Context) {
        val visitsWithAlarm = runBlocking { return@runBlocking visitRemainderRepository.getVisitCardsWithAlarms().first() }
        visitsWithAlarm.map { addVisitRemainder(context, it) }
    }

    companion object {
        const val launchRemainderAction = "LAUNCH_VISIT_REMAINDER"
        const val minutesBeforeVisit = 15L

        fun addVisitRemainder(context: Context, visitCard: VisitCard) {
            val reducedVisitData = CompactVisitData(visitCard)

            val alarmIntent = Intent(context, VisitRemainder::class.java).let { intent ->
                intent.action = launchRemainderAction
                intent.putExtra("data", reducedVisitData)
                PendingIntent.getBroadcast(context, visitCard.intId, intent, PendingIntent.FLAG_IMMUTABLE or 0)
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.set(
                AlarmManager.RTC,
                visitCard.visitDate.minusMinutes(minutesBeforeVisit).toInstant().toEpochMilli(),
                alarmIntent
            )
        }

        fun removeVisitRemainder(context: Context, visitCard: VisitCard) {
            val reducedVisitData = CompactVisitData(visitCard)

            Intent(context, VisitRemainder::class.java).let { intent ->
                intent.action = launchRemainderAction
                intent.putExtra("data", reducedVisitData)
                PendingIntent.getBroadcast(context, visitCard.intId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE)
            }?.let {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                alarmManager?.cancel(it)
            }
        }
    }
}


enum class RemainderStatus(val icon: ImageVector) {
    UNAVAILABLE(Icons.Filled.NotificationsOff),
    ON(Icons.Filled.NotificationsActive),
    OFF(Icons.Filled.NotificationsNone)
}