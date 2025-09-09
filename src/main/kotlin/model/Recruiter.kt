package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Recruiter(
    @SerialName("recruiter_id") val recruiterID: Int? = null,
    @SerialName("email") val recruiterEmail: String,
)

