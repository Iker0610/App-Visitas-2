package das.omegaterapia.visits.activities.main.screens.profile

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import das.omegaterapia.visits.preferences.IUserPreferences
import das.omegaterapia.visits.utils.AppLanguage
import das.omegaterapia.visits.utils.LanguageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


/*******************************************************************************
 ****                         Preferences View Model                        ****
 *******************************************************************************/

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: IUserPreferences,
    private val languageManager: LanguageManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /*************************************************
     **                    States                   **
     *************************************************/

    // Retrieve current logged user
    val currentUser = (savedStateHandle.get("username") as? String ?: savedStateHandle.get("LOGGED_USERNAME") as? String)!!


    /*------------------------------------------------
    |               Preferences States               |
    ------------------------------------------------*/

    // Current app's language and preferred language (may not be the same at the beginning)
    val currentSetLang by languageManager::currentLang // Used for initial flow state
    val prefLang = preferencesRepository.userLanguage(currentUser).map { AppLanguage.getFromCode(it) }

    //------   Date Time Converter Related   -------//
    val prefOneDayConverter = preferencesRepository.userDayConverter(currentUser)
    val prefMultipleDayConverter = preferencesRepository.userMultipleDayConverter(currentUser)


    /*************************************************
     **                    Events                   **
     *************************************************/

    //------   Date Time Converter Related   -------//

    fun setOneDayConverterPreference(converter: String) {
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.setUserDayConverter(currentUser, converter) }
    }

    fun setMultipleDayConverterPreference(converter: String) {
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.setUserMultipleDayConverter(currentUser, converter) }
    }


    //------------   Language Related   ------------//

    // Change language preference, adjust the locale and reload de interface
    fun changeLang(newLang: AppLanguage, context: Context) {
        languageManager.changeLang(newLang, context)
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.setUserLanguage(currentUser, newLang.code) }
    }

    // This method does not reload the interface, just adjust the locale
    fun reloadLang(lang: AppLanguage, context: Context) = languageManager.changeLang(lang, context, false)
}
