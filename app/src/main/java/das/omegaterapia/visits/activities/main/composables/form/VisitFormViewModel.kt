package das.omegaterapia.visits.activities.main.composables.form

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import das.omegaterapia.visits.model.entities.Client
import das.omegaterapia.visits.model.entities.Direction
import das.omegaterapia.visits.model.entities.VisitCard
import das.omegaterapia.visits.model.entities.VisitData
import das.omegaterapia.visits.utils.isNonEmptyText
import das.omegaterapia.visits.utils.isValidPhoneNumber
import das.omegaterapia.visits.utils.isZIP
import java.time.ZonedDateTime
import javax.inject.Inject

/*******************************************************************************
 ****                         Visit Form View Model                         ****
 *******************************************************************************/

/**
 * This ViewModel contains all the states and validations required for VisitForm interface
 */
@HiltViewModel
class VisitFormViewModel @Inject constructor() : ViewModel() {

    /*************************************************
     **                    States                   **
     *************************************************/

    //-----------   Initial Visit Card   -----------//

    private var initialVisitCard: VisitCard? = null


    //-------   Input Fields' Value States   -------//

    var isVIP by mutableStateOf(false)
    var visitDate: ZonedDateTime by mutableStateOf(ZonedDateTime.now())

    var clientNameText by mutableStateOf("")
    var clientSurnameText by mutableStateOf("")
    var clientCompanions = mutableStateListOf("")

    var phoneText by mutableStateOf("")
    var addressText by mutableStateOf("")
    var townText by mutableStateOf("")
    var zipCodeText by mutableStateOf("")

    var observationText by mutableStateOf("")


    //----   Input Fields' Validation States   -----//

    val isNameValid by derivedStateOf { isNonEmptyText(clientNameText) }
    val isSurnameValid by derivedStateOf { isNonEmptyText(clientSurnameText) }

    val isPhoneValid by derivedStateOf { isValidPhoneNumber(phoneText) }
    val isAddressValid by derivedStateOf { addressText.isNotBlank() }
    val isTownValid by derivedStateOf { townText.isNotBlank() }
    val isZIPValid by derivedStateOf { isZIP(zipCodeText) }

    val areAllValid by derivedStateOf { isNameValid && isSurnameValid && isAddressValid && isTownValid && isZIPValid && isPhoneValid }


    /*************************************************
     **                    Utils                    **
     *************************************************/

    /**
     * Initialize the states with [visitCard] values.
     * Should only be called when editing a [VisitCard] and only once per [VisitForm]
     */
    fun initializeWithVisitCard(visitCard: VisitCard) {
        if (initialVisitCard == null) {
            initialVisitCard = visitCard

            isVIP = visitCard.isVIP
            visitDate = visitCard.visitDate

            clientNameText = visitCard.client.name
            clientSurnameText = visitCard.client.surname
            clientCompanions.addAll(0, visitCard.companions)

            phoneText = visitCard.client.phoneNum
            addressText = visitCard.client.direction.address
            townText = visitCard.client.direction.town
            zipCodeText = visitCard.client.direction.zip

            observationText = visitCard.observations
        }
    }

    /**
     * Generates a [VisitCard] instance from the fields' values.
     *
     * If [initialVisitCard] it's not null, it copy that instance's values
     * and overwrites them with field's values.
     * (Always keeps the same UUID and user)
     */
    fun generateVisitCard(): VisitCard {
        val clientData = Client(
            name = clientNameText,
            surname = clientSurnameText,
            phoneNum = phoneText,
            direction = Direction(addressText, townText, zipCodeText)
        )

        val visitData: VisitData

        if (initialVisitCard != null) {
            visitData = initialVisitCard!!.visitData.copy(
                mainClientPhone = clientData.phoneNum,
                companions = clientCompanions.filter { it.isNotBlank() },
                visitDate = visitDate,
                isVIP = isVIP,
                observations = observationText
            )
        } else {
            visitData = VisitData(
                mainClientPhone = clientData.phoneNum,
                companions = clientCompanions.filter { it.isNotBlank() },
                visitDate = visitDate,
                isVIP = isVIP,
                observations = observationText
            )
        }

        return VisitCard(visitData, clientData)
    }
}