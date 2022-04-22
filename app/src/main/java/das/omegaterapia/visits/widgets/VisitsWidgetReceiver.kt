package das.omegaterapia.visits.widgets

import WidgetVisit
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import dagger.hilt.android.AndroidEntryPoint
import das.omegaterapia.visits.model.repositories.VisitsRepository
import das.omegaterapia.visits.preferences.ILoginSettings
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/*************************************************
 **               Widget Receiver               **
 *************************************************/
@AndroidEntryPoint
class VisitsWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = VisitsWidget()
    private val coroutineScope = MainScope()

    @Inject
    lateinit var visitsRepository: VisitsRepository

    @Inject
    lateinit var loginRepository: ILoginSettings

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        Log.d("Widget", "onUpdate Called")
        observeData(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.d("Widget", "onReceive Called")
        if (intent.action == VisitsWidgetRefreshCallback.UPDATE_ACTION) {
            observeData(context)
        }
    }

    private fun observeData(context: Context) {
        coroutineScope.launch {
            Log.d("Widget", "Coroutine Called")

            val currentUsername = loginRepository.getLastLoggedUser()?.username

            val visitData = if (currentUsername != null) {
                visitsRepository.getUsersTodaysVisits(currentUsername).first().map {
                    WidgetVisit(
                        isVIP = it.isVIP,
                        client = it.client.toString(),
                        hour = it.visitDate.format(DateTimeFormatter.ofPattern("HH:mm")).uppercase(),
                        phoneNumber = it.client.phoneNum,
                        shortDirection = "${it.client.direction.address}, ${it.client.direction.town}",
                        fullDirection = it.client.direction.toString()
                    )
                }
            } else emptyList()

            GlanceAppWidgetManager(context).getGlanceIds(VisitsWidget::class.java).forEach { glanceId ->
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { pref ->
                    pref.toMutablePreferences().apply {
                        this[currentUserKey] = currentUsername ?: "No User Real"
                        this[todayVisitsDataKey] = Json.encodeToString(visitData)
                    }
                }
            }
            glanceAppWidget.updateAll(context)
        }
    }

    companion object {
        val currentUserKey = stringPreferencesKey("currentUser")
        val todayVisitsDataKey = stringPreferencesKey("data")
    }
}