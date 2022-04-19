package das.omegaterapia.visits.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import das.omegaterapia.visits.model.dao.VisitsDao
import das.omegaterapia.visits.model.entities.Client
import das.omegaterapia.visits.model.entities.VisitData

/**
 * Room database definition abstract class (it's later instantiated in Hilt's module).
 *
 * Version: 1
 *
 * Entities: [VisitData], [Client]
 * Defined DAOs: [VisitsDao]
 *
 */
@Database(
    version = 2,
    entities = [VisitData::class, Client::class],
)
@TypeConverters(Converters::class)
abstract class OmegaterapiaVisitsDatabase : RoomDatabase() {
    abstract fun visitsDao(): VisitsDao
}