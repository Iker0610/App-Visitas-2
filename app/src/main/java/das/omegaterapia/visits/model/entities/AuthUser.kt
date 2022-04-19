package das.omegaterapia.visits.model.entities

import kotlinx.serialization.Serializable


/*******************************************************************************
 ****                        User Entity in Database                        ****
 *******************************************************************************/

/**
 * Data class representing the user entity. Defined by a [username] and a [password].
 */
@Serializable
data class AuthUser(
    val username: String,
    val password: String = "",
)