package das.omegaterapia.visits.model.repositories

import android.database.sqlite.SQLiteConstraintException
import das.omegaterapia.visits.model.dao.AuthenticationDao
import das.omegaterapia.visits.model.entities.AuthUser
import das.omegaterapia.visits.preferences.ILoginSettings
import javax.inject.Inject


/**
 * Interface a LoginRepository must implement.
 * It inherits from a [ILoginSettings].
 */
interface ILoginRepository : ILoginSettings {
    suspend fun getUserPassword(username: String): String?
    suspend fun createUser(authUser: AuthUser): Boolean
}


/**
 * Implementation of a ILoginRepository.
 *
 * It has all the utility required to manage authorizations
 * and unifies required access to room database and DataStore Preferences
 * in a single Repository (following the Repository design pattern).
 *
 * Required constructor parameters are injected by Hilt
 *
 * @property authDao DAO object that provides an API to access Room Database
 * @property loginSettings Object that provides an API to access DataStore Preferences
 */
class LoginRepository @Inject constructor(
    private val authDao: AuthenticationDao,
    private val loginSettings: ILoginSettings,
) : ILoginRepository {

    /*------------------------------------------------
    |           DataStore Related Methods            |
    ------------------------------------------------*/
    override suspend fun getLastLoggedUser(): String? = loginSettings.getLastLoggedUser()
    override suspend fun setLastLoggedUser(username: String) = loginSettings.setLastLoggedUser(username)


    /*------------------------------------------------
    |         Room Database Related Methods          |
    ------------------------------------------------*/

    /**
     * Given a [username] tries to fetch the user's password.
     * If the user doesn't exist the methods returns null.
     */
    override suspend fun getUserPassword(username: String): String? {
        return try {
            authDao.getUserPassword(username)
        } catch (e: SQLiteConstraintException) {
            null
        }
    }

    /**
     * Given an [AuthUser] tries to add the defined user to the database.
     * Returns true if the user has been created successfully and false otherwise.
     */
    override suspend fun createUser(authUser: AuthUser): Boolean {
        return try {
            authDao.createUser(authUser)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }
}