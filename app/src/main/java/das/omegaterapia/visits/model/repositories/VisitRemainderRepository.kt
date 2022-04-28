package das.omegaterapia.visits.model.repositories

import android.database.sqlite.SQLiteConstraintException
import das.omegaterapia.visits.model.dao.VisitAlarmDao
import das.omegaterapia.visits.model.entities.VisitAlarm
import das.omegaterapia.visits.model.entities.VisitCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Contains all the utility required to manage visit remainder status registration.
 *
 * Required constructor parameter [visitAlarmDao] is injected by Hilt.
 */
@Singleton
class VisitRemainderRepository @Inject constructor(private val visitAlarmDao: VisitAlarmDao) {
    suspend fun addRemainder(alarmVisitId: String): Boolean {
        return try {
            visitAlarmDao.addAlarm(VisitAlarm(alarmVisitId))
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    suspend fun removeRemainder(alarmVisitId: String): Boolean {
        return try {
            visitAlarmDao.removeAlarm(VisitAlarm(alarmVisitId))
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    fun getAllRemaindersAsSet(): Flow<Set<String>> = getAllRemainders().map(List<String>::toSet)
    private fun getAllRemainders(): Flow<List<String>> = visitAlarmDao.getAllAlarms().map { it.map(VisitAlarm::visitId) }
    fun getVisitCardsWithRemainders(): Flow<List<VisitCard>> =
        visitAlarmDao.getVisitCardsByIds(runBlocking { return@runBlocking getAllRemainders().first() })
}