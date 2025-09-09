package ca.uwaterloo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
data class Education(
    @SerialName("id") val id: Long? = null, //can be null and handled by db when insert
    @SerialName("user_id") val userId: String,
    @SerialName("degree_id") val degreeId: Int,
    @SerialName("institution_name") val institutionName: String,
    @SerialName("major") val major: String,
    @SerialName("gpa") val gpa: Float? = null,
    @SerialName("start_date") val startDate: LocalDate,
    @SerialName("end_date") val endDate: LocalDate,
)

