package das.omegaterapia.visits.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import das.omegaterapia.visits.model.entities.VisitAlarmEntity
import das.omegaterapia.visits.model.entities.VisitCard
import kotlinx.coroutines.flow.Flow

// TODO Anotar
@Dao
interface VisitAlarmDao {
    @Insert
    suspend fun addAlarm(alarm: VisitAlarmEntity)

    @Delete
    suspend fun removeAlarm(alarm: VisitAlarmEntity)

    @Transaction
    @Query("SELECT * FROM VisitAlarmEntity")
    fun getAllAlarms(): Flow<List<VisitAlarmEntity>>

    @Query("SELECT * FROM VisitData WHERE id = :id")
    suspend fun getVisitCardById(id: String): VisitCard

    @Query("SELECT * FROM VisitData WHERE id IN (:idList)")
    fun getVisitCardsByIds(idList: List<String>): Flow<List<VisitCard>>
}