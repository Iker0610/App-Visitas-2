package das.omegaterapia.visits.utils

import java.math.BigInteger
import java.security.MessageDigest


/*******************************************************************************
 ****                          String Hashing Utils                         ****
 *******************************************************************************/

//--------------------------------------------------------------------------------
// Original resource: https://www.knowledgefactory.net/2021/01/kotlin-hashing.html
//--------------------------------------------------------------------------------

// Instance required to hash strings with SHA-512 algorithm
private val md = MessageDigest.getInstance("SHA-512")


// Extension function for [String] class that returns the instance's hashed version.
fun String.hash(): String {
    val messageDigest = md.digest(this.toByteArray())
    val no = BigInteger(1, messageDigest)
    var hashText = no.toString(16)
    while (hashText.length < 32) {
        hashText = "0$hashText"
    }
    return hashText
}


// Extension function for [String] class that compares the current instance's hash with the given one.
fun String.compareHash(hashedString: String?): Boolean = this.hash() == hashedString
