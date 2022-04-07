package das.omegaterapia.visits.activities.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.model.entities.VisitId
import das.omegaterapia.visits.model.repositories.IVisitsRepository
import das.omegaterapia.visits.preferences.IUserPreferences
import das.omegaterapia.visits.utils.TemporalConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.format.DateTimeFormatter
import javax.inject.Inject


/*******************************************************************************
 ****                           Visits View Model                           ****
 *******************************************************************************/

@HiltViewModel
class VisitsViewModel @Inject constructor(
    private val visitsRepository: IVisitsRepository,
    private val preferencesRepository: IUserPreferences,

    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    /*
    // Data generator code to populate original database (right now it has no purpose)

    init {
        viewModelScope.launch {
            val user = savedStateHandle.get("LOGGED_USERNAME") as? String ?: ""
            visitsRepository.addVisitCards(visitList.map { it.user = user; it.visitData.mainClientPhone = it.client.phoneNum; return@map it })
        }
    }
    */

    /*************************************************
     **                    States                   **
     *************************************************/

    val currentUser = savedStateHandle.get("LOGGED_USERNAME") as? String ?: ""

    val allVisits = visitsRepository.getUsersVisits(currentUser)
        // Edit the flow to group list items by date with the user selected grouping
        .map { visitList -> getMultipleDayFormatter().groupDates(visitList, key = VisitCard::visitDate::get) }

    val vipVisits = visitsRepository.getUsersVIPVisits(currentUser)
        // Edit the flow to group list items by date with the user selected grouping
        .map { visitList -> getMultipleDayFormatter().groupDates(visitList, key = VisitCard::visitDate::get) }

    val todaysVisits = visitsRepository.getUsersTodaysVisits(currentUser)
        // Edit the flow to group list items by date with the user selected grouping
        .map { visitList -> getDayFormatter().groupDates(visitList, key = VisitCard::visitDate::get) }


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

    suspend fun updateVisitCard(visitCard: VisitCard) = visitsRepository.updateVisitCard(visitCard)

    fun deleteVisitCard(visitId: VisitId) = viewModelScope.launch(Dispatchers.IO) { visitsRepository.deleteVisitCard(visitId) }


    /*************************************************
     **                    Utils                    **
     *************************************************/

    fun todaysVisitsJson(): String {
        val gsonBuilder = GsonBuilder().setPrettyPrinting().create()

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

            return@runBlocking gsonBuilder.toJson(todaysVisit)
        }
    }
}