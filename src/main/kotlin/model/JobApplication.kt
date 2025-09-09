package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JobApplication(
    @SerialName("app_id") val appID: String? = null, //can be null and handled by db when insert
    @SerialName("created_at") val createdAt: String? = null, //can be null and handled by db when insert
    @SerialName("user_id") val userId: String,
    @SerialName("job_title") val jobTitle: String,
    @SerialName("company_name") val companyName: String,
    @SerialName("city_id") val cityId: Int? = null,
    @SerialName("app_status_id") val appStatusId: Int,
    @SerialName("recruiter_email_id") val recruiterEmailID: Int
)

