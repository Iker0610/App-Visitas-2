package das.omegaterapia.visits.preferences

import das.omegaterapia.visits.model.entities.AuthUser

// Interface for accessing authentication related settings
interface ILoginSettings {
    suspend fun getLastLoggedUser(): AuthUser?
    suspend fun setLastLoggedUser(user: AuthUser)
}