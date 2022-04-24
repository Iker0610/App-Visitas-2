package das.omegaterapia.visits.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import das.omegaterapia.visits.NotificationChannelID
import das.omegaterapia.visits.NotificationID
import das.omegaterapia.visits.R
import das.omegaterapia.visits.model.entities.CompactVisitData
import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.model.repositories.VisitAlarmRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


// TODO: Anotar
@AndroidEntryPoint
class VisitAlarm : BroadcastReceiver() {

    @Inject
    lateinit var visitAlarmRepository: VisitAlarmRepository


    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "android.intent.action.BOOT_COMPLETED" -> reloadAlarms(context)
            launchAlarmAction -> launchVisitAlarmNotification(context, intent.getParcelableExtra("data")!!)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun launchVisitAlarmNotification(context: Context, visit: CompactVisitData) {
        // Delete alarm from database
        GlobalScope.launch(Dispatchers.IO) { visitAlarmRepository.removeAlarm(visit.id) }

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
        val visitsWithAlarm = runBlocking { return@runBlocking visitAlarmRepository.getVisitCardsWithAlarms().first() }
        visitsWithAlarm.map { addVisitAlarm(context, it) }
    }

    companion object {
        const val launchAlarmAction = "LAUNCH_VISIT_ALARM"

        fun addVisitAlarm(context: Context, visitCard: VisitCard) {
            val reducedVisitData = CompactVisitData(visitCard)

            val alarmIntent = Intent(context, VisitAlarm::class.java).let { intent ->
                intent.action = launchAlarmAction
                intent.putExtra("data", reducedVisitData)
                PendingIntent.getBroadcast(context, visitCard.intId, intent, 0)
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.set(
                AlarmManager.RTC,
                visitCard.visitDate.minusMinutes(15).toInstant().toEpochMilli(),
                alarmIntent
            )
        }

        fun deleteVisitAlarm(context: Context, visitCard: VisitCard) {
            val reducedVisitData = CompactVisitData(visitCard)

            Intent(context, VisitAlarm::class.java).let { intent ->
                intent.action = launchAlarmAction
                intent.putExtra("data", reducedVisitData)
                PendingIntent.getBroadcast(context, visitCard.intId, intent, PendingIntent.FLAG_NO_CREATE)
            }?.let {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                alarmManager?.cancel(it)
            }
        }
    }
}