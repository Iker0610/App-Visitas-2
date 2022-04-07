package das.omegaterapia.visits.model.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import das.omegaterapia.visits.model.entities.AuthUser


/**
 * DAO defining the room database access API for authentication purposes.
 *
 * It includes adding an user and and retrieving passwords given the username.
 */
@Dao
interface AuthenticationDao {
    /**
     * Tries to add [authUser] to the database.
     * Throws an [SQLiteConstraintException] if a user already exists with that username.
     */
    @Insert
    suspend fun createUser(authUser: AuthUser)

    /**
     * Tries to retrieve [username]'s password.
     * Throws an [SQLiteConstraintException] if there's no user with that password.
     */
    @Transaction
    @Query("SELECT hashed_password FROM User WHERE username = :username")
    suspend fun getUserPassword(username: String): String
}