package das.omegaterapia.visits.widgets

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
import das.omegaterapia.visits.model.entities.CompactVisitData
import das.omegaterapia.visits.model.repositories.VisitsRepository
import das.omegaterapia.visits.preferences.ILoginSettings
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject


/*******************************************************************************
 ****                            Widget Receiver                            ****
 *******************************************************************************/

/**
 * Widget Receiver in charge of handling various events, primary [onUpdate] and [onReceive] events.
 *
 * This widget gets data from various repositories process it and saves an updated and processed version
 * in each widget's datastore.
 *
 * Repositories are injected via Hilt.
 */
@AndroidEntryPoint
class VisitsWidgetReceiver : GlanceAppWidgetReceiver() {

    /*************************************************
     **                  Attributes                 **
     *************************************************/

    //-----------------   Widget   -----------------//
    override val glanceAppWidget: GlanceAppWidget = VisitsWidget()


    //----------------   Utility   -----------------//
    private val coroutineScope = MainScope()


    //-----------   Data Repositories   ------------//
    @Inject
    lateinit var visitsRepository: VisitsRepository

    @Inject
    lateinit var loginRepository: ILoginSettings


    /*************************************************
     **                    Events                   **
     *************************************************/

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        Log.d("Widget", "onUpdate Called")
        observeData(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.d("Widget", "onReceive Called; Action: ${intent.action}")

        // We filter actions in order to prevent updating twice in onUpdate events.
        if (intent.action == UPDATE_ACTION) {
            observeData(context)
        }
    }


    /*************************************************
     **            Data Fetch and Update            **
     *************************************************/

    private fun observeData(context: Context) {
        coroutineScope.launch {
            Log.d("Widget", "Coroutine Called")

            // Get last logged user or null
            val currentUsername = loginRepository.getLastLoggedUser()?.username

            // If there's  a user get it's visits
            val visitData = if (currentUsername != null) {
                visitsRepository.getUsersTodaysVisits(currentUsername).first().map(::CompactVisitData)
            } else emptyList()


            Log.d("Widget", "Coroutine - Data-Length: ${visitData.size}")

            // Get all the widget IDs and update them
            GlanceAppWidgetManager(context).getGlanceIds(VisitsWidget::class.java).forEach { glanceId ->
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { widgetDataStore ->
                    widgetDataStore.toMutablePreferences().apply {

                        // If there's a user update data.
                        if (currentUsername != null) {
                            this[currentUserKey] = currentUsername
                            this[todayVisitsDataKey] = Json.encodeToString(visitData)
                        }

                        // If there's no user clear all data
                        else this.clear()

                    }
                }
            }

            // Force widget update
            glanceAppWidget.updateAll(context)
        }
    }


    /*******************************************************************************
     ****                               Constants                               ****
     *******************************************************************************/

    companion object {

        const val UPDATE_ACTION = "updateAction"

        val currentUserKey = stringPreferencesKey("currentUser")
        val todayVisitsDataKey = stringPreferencesKey("data")
    }
}