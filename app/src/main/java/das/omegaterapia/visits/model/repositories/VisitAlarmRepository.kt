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

@Singleton
class VisitAlarmRepository @Inject constructor(private val visitAlarmDao: VisitAlarmDao) {
    suspend fun addAlarm(alarmVisitId: String): Boolean {
        return try {
            visitAlarmDao.addAlarm(VisitAlarm(alarmVisitId))
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    suspend fun removeAlarm(alarmVisitId: String): Boolean {
        return try {
            visitAlarmDao.removeAlarm(VisitAlarm(alarmVisitId))
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    fun getAllAlarmsAsSet(): Flow<Set<String>> = getAllAlarms().map(List<String>::toSet)
    private fun getAllAlarms(): Flow<List<String>> = visitAlarmDao.getAllAlarms().map { it.map(VisitAlarm::visitId) }
    fun getVisitCardsWithAlarms(): Flow<List<VisitCard>> = visitAlarmDao.getVisitCardsByIds(runBlocking { return@runBlocking getAllAlarms().first() })
}