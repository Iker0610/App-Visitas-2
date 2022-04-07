package das.omegaterapia.visits.model.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.ZonedDateTime
import java.util.*


/*******************************************************************************
 ****                     Visit Card Entity in Database                     ****
 *******************************************************************************/

/**
 * POJO that contains the [VisitCard] id.
 * Used for delete operation, that needs an instance of the entity class [VisitData] or a POJO with the attributes defined.
 */
data class VisitId(val id: String)


/*************************************************
 **              Visit Data Entity              **
 *************************************************/

/**
 * Data class representing the [VisitData] entity.
 *
 *
 * @property id Random unique ID for the visit.
 * @property user Owner of the visit card.
 * @property mainClientPhone References the [Client] of the visit.
 * @property companions People that will be with the [Client].
 * @property visitDate When the visit will take place.
 * @property observations
 * @property isVIP If the visit is to a previous client
 */
@Entity(
    // Entity's foreign keys to [AuthUser] and [Client]
    foreignKeys = [
        ForeignKey(entity = AuthUser::class, parentColumns = ["username"], childColumns = ["user"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Client::class, parentColumns = ["phone_number"], childColumns = ["main_client_phone"], onDelete = ForeignKey.CASCADE)
    ],

    // Indexes for faster queries
    indices = [Index(value = ["user", "visit_date"]), Index(value = ["main_client_phone"])],
)
data class VisitData(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    var user: String = "",

    @ColumnInfo(name = "main_client_phone")
    var mainClientPhone: String,

    var companions: List<String> = listOf(),

    @ColumnInfo(name = "visit_date")
    var visitDate: ZonedDateTime,

    var observations: String = "",

    @ColumnInfo(name = "is_VIP")
    var isVIP: Boolean = false,
)


/*************************************************
 **              Visit Card Entity              **
 *************************************************/

/**
 * Data class to represent the relation between a [VisitData] and [Client]
 * and have fast access to both in the same instance (easing data structure manipulation).
 */
data class VisitCard(

    // Main entity (the visit data)
    @Embedded var visitData: VisitData,

    // Entity obtained with the relationship
    @Relation(parentColumn = "main_client_phone", entityColumn = "phone_number", entity = Client::class)
    var client: Client,
) {
    /*************************************************
     **             Delegated Properties            **
     *************************************************/

    /*
     * These are delegated properties.
     *
     * These properties' (classic java attributes) getters and setters are delegated to their [VisitData] counterparts.
     * The reason for these delegation is avoiding repetitive code. Example:
     *
     * Without property delegation  ->  myVisitCard.visitData.observations
     * With property delegation:    ->  myVisitCard.observations
     *
     * Property delegation DO NOT copy values, they directly delegate getters and setters.
     * These properties are marked with @delegate:Ignore so that the Room Database doesn't try to fill them
     */

    @delegate:Ignore
    val id by visitData::id

    @delegate:Ignore
    var user by visitData::user

    @delegate:Ignore
    val companions by visitData::companions

    @delegate:Ignore
    val visitDate by visitData::visitDate

    @delegate:Ignore
    val observations by visitData::observations

    @delegate:Ignore
    val isVIP by visitData::isVIP
}
