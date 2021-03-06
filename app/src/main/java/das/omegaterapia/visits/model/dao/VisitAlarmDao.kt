package das.omegaterapia.visits.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import das.omegaterapia.visits.model.entities.VisitAlarm
import das.omegaterapia.visits.model.entities.VisitCard
import kotlinx.coroutines.flow.Flow

/**
 * DAO defining the room database access API related to alarm management
 */
@Dao
interface VisitAlarmDao {
    @Insert
    suspend fun addAlarm(alarm: VisitAlarm)

    @Delete
    suspend fun removeAlarm(alarm: VisitAlarm)

    @Transaction
    @Query("SELECT * FROM VisitAlarm")
    fun getAllAlarms(): Flow<List<VisitAlarm>>

    @Transaction
    @Query("SELECT * FROM VisitData WHERE id IN (:idList)")
    fun getVisitCardsByIds(idList: List<String>): Flow<List<VisitCard>>
}