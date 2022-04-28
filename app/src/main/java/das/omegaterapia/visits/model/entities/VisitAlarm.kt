package das.omegaterapia.visits.model.entities


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


/**
 * Data class representing the VisitAlarm weak entity.
 *
 * @property visitId: id of the [VisitData] that has a remainder set.
 */
@Entity(
    // Entity's foreign keys to [VisitData]
    foreignKeys = [
        ForeignKey(entity = VisitData::class,
            parentColumns = ["id"],
            childColumns = ["visitId"],
            onDelete = ForeignKey.CASCADE)
    ]
)
data class VisitAlarm(
    @PrimaryKey val visitId: String,
)
