package das.omegaterapia.visits.preferences

import kotlinx.coroutines.flow.Flow

// Interface to access a given user preferences
interface IUserPreferences {
    fun userLanguage(user: String): Flow<String>
    suspend fun setUserLanguage(user: String, langCode: String)

    fun userDayConverter(user: String): Flow<String>
    suspend fun setUserDayConverter(user: String, converter: String)

    fun userMultipleDayConverter(user: String): Flow<String>
    suspend fun setUserMultipleDayConverter(user: String, converter: String)
}