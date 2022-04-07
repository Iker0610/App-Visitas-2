package das.omegaterapia.visits.model.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


/*******************************************************************************
 ****                       Client Entity in Database                       ****
 *******************************************************************************/


/*************************************************
 **              Client's Direction             **
 *************************************************/
data class Direction(
    var address: String,
    var town: String,
    var zip: String,
) {
    override fun toString(): String {
        return "$address, $town $zip"
    }
}


/*************************************************
 **                Client's Data                **
 *************************************************/

/**
 * Data class representing the Client entity.
 *
 * It is represented by [name] and [surname], a [direction] and a [phoneNum].
 * [phoneNum] is the primary key of the entity.
 *
 * [direction] is [Embedded], what means that in the database table of this entity,
 * the fields defined in [Direction] class will be normal columns of [Client] entity.
 */
@Entity(indices = [Index(value = ["town", "zip"])])
data class Client(
    var name: String,
    var surname: String,

    @ColumnInfo(name = "phone_number")
    @PrimaryKey var phoneNum: String,

    @Embedded
    var direction: Direction,
) {
    override fun toString(): String {
        return "$name $surname"
    }
}

