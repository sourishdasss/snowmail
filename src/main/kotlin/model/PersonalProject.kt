package ca.uwaterloo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalProject(
    @SerialName("id") val id: Long? = null, //can be null and handled by db when insert
    @SerialName("user_id") val userId: String,
    @SerialName("project_name") val projectName: String,
    @SerialName("description") val description: String?
)
