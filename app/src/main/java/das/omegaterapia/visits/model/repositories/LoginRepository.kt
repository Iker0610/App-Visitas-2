package das.omegaterapia.visits.model.repositories

import das.omegaterapia.visits.model.entities.AuthUser
import das.omegaterapia.visits.preferences.ILoginSettings
import das.omegaterapia.visits.utils.AuthenticationClient
import das.omegaterapia.visits.utils.AuthenticationException
import das.omegaterapia.visits.utils.UserExistsException
import javax.inject.Inject


/**
 * Interface a LoginRepository must implement.
 * It inherits from a [ILoginSettings].
 */
interface ILoginRepository : ILoginSettings {
    suspend fun createUser(authUser: AuthUser): Boolean
    suspend fun authenticateUser(authUser: AuthUser): Boolean
}


/**
 * Implementation of a [ILoginRepository].
 *
 * It has all the utility required to manage authorizations
 * and unifies required access to api server and DataStore Preferences
 * in a single Repository (following the Repository design pattern).
 *
 * Required constructor parameters are injected by Hilt
 *
 * @property authenticationClient Client to authorize and create users
 * @property loginSettings Object that provides an API to access DataStore Preferences
 */
class LoginRepository @Inject constructor(
    private val authenticationClient: AuthenticationClient,
    private val loginSettings: ILoginSettings,
) : ILoginRepository {

    /*------------------------------------------------
    |           DataStore Related Methods            |
    ------------------------------------------------*/
    override suspend fun getLastLoggedUser(): AuthUser? = loginSettings.getLastLoggedUser()
    override suspend fun setLastLoggedUser(user: AuthUser) = loginSettings.setLastLoggedUser(user)


    /*------------------------------------------------
    |         Room Database Related Methods          |
    ------------------------------------------------*/

    /**
     * Given a [authUser] tries to authenticate its credentials.
     * Returns True on success and False otherwise.
     *
     * In case of an unexpected error, throws an exception
     */
    @Throws(Exception::class)
    override suspend fun authenticateUser(authUser: AuthUser): Boolean {
        return try {
            authenticationClient.authenticate(authUser)
            true
        } catch (e: AuthenticationException) {
            false
        }
    }


    /**
     * Given an [authUser] tries to add the defined user to the database.
     * Returns true if the user has been created successfully and false otherwise.
     */
    override suspend fun createUser(authUser: AuthUser): Boolean {
        return try {
            authenticationClient.createUser(authUser)
            true
        } catch (e: UserExistsException) {
            false
        }
    }
}