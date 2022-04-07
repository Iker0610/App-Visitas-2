package das.omegaterapia.visits.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/*******************************************************************************
 ****                        User Entity in Database                        ****
 *******************************************************************************/

/**
 * Data class representing the user entity. Defined by a [username] and a [hashedPassword].
 * [hashedPassword] must be given already hashed.
 */
@Entity(tableName = "User")
data class AuthUser(
    @PrimaryKey val username: String,
    @ColumnInfo(name = "hashed_password") val hashedPassword: String,
)