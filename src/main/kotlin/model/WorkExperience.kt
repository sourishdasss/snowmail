package ca.uwaterloo.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName

@Serializable
data class WorkExperience(
    @SerialName("id") val id: Long? = null, //can be null and handled by db when insert
    @SerialName("user_id") val userId: String,
    @SerialName("company_name") val companyName: String,
    @SerialName("currently_working") val currentlyWorking: Boolean,
    @SerialName("title") val title: String,
    @SerialName("start_date") val startDate: LocalDate,
    @SerialName("end_date") val endDate: LocalDate,
    @SerialName("description") val description: String?
)
