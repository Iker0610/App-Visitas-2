package das.omegaterapia.visits.activities.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.model.entities.VisitId
import das.omegaterapia.visits.model.repositories.IVisitsRepository
import das.omegaterapia.visits.model.repositories.VisitRemainderRepository
import das.omegaterapia.visits.preferences.IUserPreferences
import das.omegaterapia.visits.services.RemainderStatus
import das.omegaterapia.visits.services.VisitRemainder.Companion.minutesBeforeVisit
import das.omegaterapia.visits.utils.TemporalConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


/*******************************************************************************
 ****                           Visits View Model                           ****
 *******************************************************************************/

@HiltViewModel
class VisitsViewModel @Inject constructor(
    private val visitsRepository: IVisitsRepository,
    private val preferencesRepository: IUserPreferences,
    private val visitRemainderRepository: VisitRemainderRepository,

    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /*
    // Data generator code to populate original database (right now it has no purpose)
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val user = savedStateHandle.get("LOGGED_USERNAME") as? String ?: ""
            val visitCards = visitList.map { it.user = user; it.visitData.mainClientPhone = it.client.phoneNum; return@map it }
            Log.d("DATABASE", visitCards.map { it.id }.toString())
            Log.d("DATABASE", Json.encodeToString(visitsRepository.addVisitCards(visitCards)))
        }
    }
    */


    /*************************************************
     **                    States                   **
     *************************************************/

    val currentUser = savedStateHandle.get("LOGGED_USERNAME") as? String ?: ""

    private val allVisits = visitsRepository.getUsersVisits(currentUser)

    val groupedAllVisits = allVisits
        // Edit the flow to group list items by date with the user selected grouping
        .map { visitList -> getMultipleDayFormatter().groupDates(visitList, key = VisitCard::visitDate::get) }

    val groupedVipVisits = visitsRepository.getUsersVIPVisits(currentUser)
        // Edit the flow to group list items by date with the user selected grouping
        .map { visitList -> getMultipleDayFormatter().groupDates(visitList, key = VisitCard::visitDate::get) }

    val todayVisits = visitsRepository.getUsersTodaysVisits(currentUser)

    val groupedTodaysVisits = todayVisits
        // Edit the flow to group list items by date with the user selected grouping
        .map { visitList -> getDayFormatter().groupDates(visitList, key = VisitCard::visitDate::get) }


    private val refreshFlow: Flow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(1000)
        }
    }

    private val currentRemainders = visitRemainderRepository.getAllAlarmsAsSet()

    val visitsRemainderStatuses = combine(allVisits, currentRemainders, refreshFlow) { visitList, currentRemainders, _ ->
        visitList.associate { visit ->
            visit.id to when {
                visit.visitDate.minusMinutes(minutesBeforeVisit).withSecond(0) <= ZonedDateTime.now() -> RemainderStatus.UNAVAILABLE
                visit.id in currentRemainders -> RemainderStatus.ON
                else -> RemainderStatus.OFF
            }
        }
    }


    // It should be null always except on Edit Visit Screen
    var currentToEditVisit: VisitCard? by mutableStateOf(null)


    /*
     * Methods to get the user's preferred TemporalConverter, they get the first item of the flow and return it.
     * They are synchronous methods.
     */
    private fun getDayFormatter(): TemporalConverter {
        return runBlocking {
            return@runBlocking preferencesRepository.userDayConverter(currentUser).map { TemporalConverter.valueOf(it) }.first()
        }
    }

    private fun getMultipleDayFormatter(): TemporalConverter {
        return runBlocking {
            return@runBlocking preferencesRepository.userMultipleDayConverter(currentUser).map { TemporalConverter.valueOf(it) }.first()
        }
    }


    /*************************************************
     **                    Events                   **
     *************************************************/

    suspend fun addVisitCard(visitCard: VisitCard) = visitsRepository.addVisitCard(visitCard.also { it.user = currentUser })

    suspend fun updateVisitCard(visitCard: VisitCard): Boolean {
        return visitsRepository.updateVisitCard(visitCard)
    }

    fun deleteVisitCard(visitId: VisitId) = viewModelScope.launch(Dispatchers.IO) {
        visitsRepository.deleteVisitCard(visitId)
        if (visitsRemainderStatuses.first()[visitId.id] == RemainderStatus.ON) {
            visitRemainderRepository.removeAlarm(visitId.id)
        }
    }

    suspend fun addVisitRemainder(visitCard: VisitCard) = visitRemainderRepository.addAlarm(visitCard.id)
    suspend fun removeVisitRemainder(visitCard: VisitCard) = visitRemainderRepository.removeAlarm(visitCard.id)

    /*************************************************
     **                    Utils                    **
     *************************************************/
    suspend fun getVisitCard(visitId: String) = allVisits.first().asSequence().filter { it.id == visitId }.firstOrNull()

    fun todaysVisitsJson(): String {
        val json = Json { prettyPrint = true }

        return runBlocking {
            val todaysVisit = visitsRepository.getUsersTodaysVisits(currentUser).first()
                .map { visitCard ->
                    mapOf(
                        "Date-Time" to visitCard.visitDate.format(DateTimeFormatter.ISO_DATE_TIME),
                        "Client" to visitCard.client.toString(),
                        "Direction" to visitCard.client.direction.toString(),
                        "Phone" to visitCard.client.phoneNum,
                        "VIP" to visitCard.isVIP.toString()
                    )
                }

            return@runBlocking json.encodeToString(todaysVisit)
        }
    }
}