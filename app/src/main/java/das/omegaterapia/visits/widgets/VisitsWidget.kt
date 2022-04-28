package das.omegaterapia.visits.widgets


import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import das.omegaterapia.visits.R
import das.omegaterapia.visits.model.entities.CompactVisitData
import das.omegaterapia.visits.widgets.VisitsWidgetReceiver.Companion.currentUserKey
import das.omegaterapia.visits.widgets.VisitsWidgetReceiver.Companion.todayVisitsDataKey
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.text.split
import kotlin.text.uppercase

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
        val context = LocalContext.current
        val prefs = currentState<Preferences>()

        val user = prefs[currentUserKey]
        val data: String? = prefs[todayVisitsDataKey]

        val visitList: List<CompactVisitData> = if (data != null) Json.decodeFromString(data) else emptyList()

        Column(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            modifier = GlanceModifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(16.dp)
        ) {

            Text(
                text = if (user != null) context.getString(R.string.widget_title, user) else context.getString(R.string.widget_title_no_user),
                modifier = GlanceModifier.fillMaxWidth().padding(bottom = 16.dp),
                style = Typography.H6.style,
                maxLines = 1
            )

            when {

                user == null -> {
                    Column(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.fillMaxSize().defaultWeight()
                    ) {
                        Text(text = context.getString(R.string.widget_no_user_content))
                    }
                }
                visitList.isEmpty() -> {
                    Column(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.fillMaxSize().defaultWeight()
                    ) {
                        Text(text = context.getString(R.string.widget_empty_list))
                    }
                }
                else -> {
                    LazyColumn(modifier = GlanceModifier.fillMaxSize().defaultWeight()) {
                        items(visitList, itemId = { it.hashCode().toLong() }) { item ->
                            VisitItem(visit = item)
                        }
                    }
                }
            }


            Spacer(GlanceModifier.height(8.dp))

            Image(
                provider = ImageProvider(R.drawable.widget_refresh_icon),
                contentDescription = null,
                modifier = GlanceModifier.size(24.dp).clickable(actionRunCallback<VisitsWidgetRefreshCallback>())
            )
        }
    }

    @Composable
    private fun VisitItem(visit: CompactVisitData) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier.fillMaxWidth().defaultWeight()
            ) {
                Column {
                    val hourMinute = visit.hour.split(':')
                    Text(text = hourMinute[0], style = Typography.REMARKED.style)
                    Text(text = hourMinute[1], style = Typography.REMARKED.style)
                }

                Spacer(GlanceModifier.width(16.dp))

                Column {
                    Text(text = visit.shortDirection.uppercase(), modifier = GlanceModifier.defaultWeight(), style = Typography.OVERLINE.style)
                    Text(text = visit.client, modifier = GlanceModifier.defaultWeight(), style = Typography.BODY2.style)
                }
            }

            Row(horizontalAlignment = Alignment.CenterHorizontally, modifier = GlanceModifier.padding(start = 16.dp)) {
                Image(
                    provider = ImageProvider(R.drawable.widget_call_icon),
                    contentDescription = null,
                    modifier = GlanceModifier.size(18.dp).clickable(
                        actionStartActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", visit.phoneNumber, null)))
                    )
                )

                Spacer(GlanceModifier.width(16.dp))

                Image(
                    provider = ImageProvider(R.drawable.widget_map_icon),
                    contentDescription = null,
                    modifier = GlanceModifier.size(18.dp).clickable(
                        actionStartActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(visit.fullDirection))))
                    )
                )
            }
        }
    }
}


private enum class Typography(val style: TextStyle) {
    H6(
        TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    ),

    BODY2(
        TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )
    ),

    OVERLINE(
        TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp
        )
    ),

    REMARKED(
        TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    )
}


/*************************************************
 **                  Callbacks                  **
 *************************************************/

class VisitsWidgetRefreshCallback : ActionCallback {

    override suspend fun onRun(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val intent = Intent(context, VisitsWidgetReceiver::class.java).apply { action = UPDATE_ACTION }
        context.sendBroadcast(intent)
    }

    companion object {
        const val UPDATE_ACTION = "updateAction"
    }
}