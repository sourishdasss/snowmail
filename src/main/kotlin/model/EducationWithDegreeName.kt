package ca.uwaterloo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
data class EducationWithDegreeName(
    val id: Long? = null, //can be null and handled by db when insert
    val userId: String,
    val degreeName: String,
    val institutionName: String,
    val major: String,
    val gpa: Float? = null,
    val startDate: LocalDate,
    val endDate: LocalDate,
)

