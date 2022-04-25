package das.omegaterapia.visits.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import das.omegaterapia.visits.model.dao.VisitAlarmDao
import das.omegaterapia.visits.model.dao.VisitsDao
import das.omegaterapia.visits.model.entities.Client
import das.omegaterapia.visits.model.entities.VisitAlarm
import das.omegaterapia.visits.model.entities.VisitData

/**
 * Room database definition abstract class (it's later instantiated in Hilt's module).
 *
 * Version: 1
 *
 * Entities: [VisitData], [Client], [VisitData]
 * Defined DAOs: [VisitsDao], [VisitAlarmDao]
 *
 */
@Database(
    version = 3,
    entities = [VisitData::class, Client::class, VisitAlarm::class],
)
@TypeConverters(Converters::class)
abstract class OmegaterapiaVisitsDatabase : RoomDatabase() {
    abstract fun visitsDao(): VisitsDao
    abstract fun visitAlarmDao(): VisitAlarmDao
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """CREATE TABLE `VisitAlarm` (
                |`visitId` TEXT NOT NULL, 
                |PRIMARY KEY(`visitId`), 
                |FOREIGN KEY (visitId) REFERENCES VisitData(id)
                |ON DELETE CASCADE
                |)""".trimMargin()
        )
    }
}
