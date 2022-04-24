package das.omegaterapia.visits.model.entities


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


// TODO: Anotar
@Entity(
    // Entity's foreign keys to [VisitData]
    foreignKeys = [
        ForeignKey(entity = VisitData::class,
            parentColumns = ["id"],
            childColumns = ["visitId"],
            onDelete = ForeignKey.CASCADE)
    ]
)
data class VisitAlarmEntity(
    @PrimaryKey val visitId: String,
)
