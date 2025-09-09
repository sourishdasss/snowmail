package model

import kotlinx.serialization.Serializable

@Serializable
data class UserInput(
    val jobDescription: String?,
    val jobTitle: String?,
    val company: String?,
    val recruiterName: String?,
    val recruiterEmail: String?,
    var fileURLs: List<String>?,
)
