package das.omegaterapia.visits.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import das.omegaterapia.visits.model.dao.AuthenticationDao
import das.omegaterapia.visits.model.dao.VisitsDao
import das.omegaterapia.visits.model.entities.AuthUser
import das.omegaterapia.visits.model.entities.Client
import das.omegaterapia.visits.model.entities.VisitData


/**
 * Room database definition abstract class (it's later instantiated in Hilt's module).
 *
 * Version: 1
 *
 * Entities: [AuthUser], [VisitData], [Client]
 * Defined DAOs: [AuthenticationDao], [VisitsDao]
 *
 */
@Database(entities = [AuthUser::class, VisitData::class, Client::class], version = 1)
@TypeConverters(Converters::class)
abstract class OmegaterapiaVisitsDatabase : RoomDatabase() {
    abstract fun authenticationDao(): AuthenticationDao
    abstract fun visitsDao(): VisitsDao
}