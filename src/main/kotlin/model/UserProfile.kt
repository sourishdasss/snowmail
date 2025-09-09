package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    @SerialName("user_id") var userId: String,
    @SerialName("first_name") var firstName: String,
    @SerialName("last_name") var lastName: String,
    @SerialName("email") var email: String? = null,
    @SerialName("city_name") var cityName: String? = null,
    @SerialName("resume_url") val resumeUrl: String? = null,
    @SerialName("linkedin_url") val linkedinUrl: String? = null,
    @SerialName("github_url") val githubUrl: String? = null,
    @SerialName("personal_website_url") val personalWebsiteUrl: String? = null,
    @SerialName("phone") var phone: String? = null,
    @SerialName("linked_gmail_account") val linkedGmailAccount: String? = null,
    @SerialName("gmail_app_password") val gmailAppPassword: String? = null
)
