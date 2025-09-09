package model

import kotlinx.serialization.Serializable

@Serializable
data class GeneratedEmail(
    val subject: String?,
    val body: String?
)
