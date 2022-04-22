import kotlinx.serialization.Serializable

@Serializable
data class WidgetVisit(
    val isVIP: Boolean,
    val hour: String,
    val client: String,
    val phoneNumber: String,
    val shortDirection: String,
    val fullDirection: String,
)