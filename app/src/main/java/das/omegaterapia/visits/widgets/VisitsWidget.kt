package das.omegaterapia.visits.widgets


import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dagger.hilt.android.AndroidEntryPoint
import das.omegaterapia.visits.model.repositories.VisitsRepository
import das.omegaterapia.visits.preferences.ILoginSettings
import das.omegaterapia.visits.widgets.VisitsWidgetReceiver.Companion.currentUserKey
import das.omegaterapia.visits.widgets.VisitsWidgetReceiver.Companion.todayVisitsDataKey
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/*******************************************************************************
 ****                             Visits Widget                             ****
 *******************************************************************************/


/*************************************************
 **                    Widget                   **
 *************************************************/

class VisitsWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    @Composable
    override fun Content() {

        val prefs = currentState<Preferences>()
        val user = prefs[currentUserKey] ?: "No User"
        val data: String? = prefs[todayVisitsDataKey]
        val visitList: List<WidgetVisit> = if (data != null) Json.decodeFromString(data) else emptyList()

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(16.dp)
        ) {
            Button(
                text = "Refresh",
                onClick = actionRunCallback<VisitsWidgetRefreshCallback>()
            )


            Text(
                text = "First Glance widget $user",
                modifier = GlanceModifier.fillMaxWidth().padding(bottom = 16.dp),
                style = TextStyle(fontWeight = FontWeight.Bold),
            )

            LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                items(visitList) { item ->
                    VisitItem(visit = item)
                }
            }
        }
    }

    @Composable
    private fun VisitItem(visit: WidgetVisit) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(8.dp)
        ) {

            Text(text = visit.hour)
            Spacer(GlanceModifier.width(8.dp))

            Text(text = visit.client)
        }
    }
}


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
                        phoneNumber = it.client.phoneNum
                    )
                }
            } else emptyList()

            val glanceId = GlanceAppWidgetManager(context).getGlanceIds(VisitsWidget::class.java).firstOrNull()
            glanceId?.let {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                    pref.toMutablePreferences().apply {
                        this[currentUserKey] = currentUsername ?: "No User Real"
                        this[todayVisitsDataKey] = Json.encodeToString(visitData)
                    }
                }
                glanceAppWidget.update(context, it)
            }
        }
    }

    companion object {
        val currentUserKey = stringPreferencesKey("currentUser")
        val todayVisitsDataKey = stringPreferencesKey("data")
    }
}


class VisitsWidgetRefreshCallback : ActionCallback {

    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val intent = Intent(context, VisitsWidgetReceiver::class.java).apply { action = UPDATE_ACTION }
        context.sendBroadcast(intent)
    }

    companion object {
        const val UPDATE_ACTION = "updateAction"
    }

}

@Serializable
private data class WidgetVisit(
    val isVIP: Boolean,
    val hour: String,
    val client: String,
    val phoneNumber: String,
)