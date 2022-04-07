package das.omegaterapia.visits.preferences

// Interface for accessing authentication related settings
interface ILoginSettings {
    suspend fun getLastLoggedUser(): String?
    suspend fun setLastLoggedUser(username: String)
}