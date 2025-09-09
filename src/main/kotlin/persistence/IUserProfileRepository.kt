package ca.uwaterloo.persistence

import ca.uwaterloo.model.Education
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.PersonalProject
import ca.uwaterloo.model.WorkExperience
import model.UserProfile
import kotlinx.datetime.LocalDate

interface IUserProfileRepository {
    suspend fun getUserLinkedGmailAccount(userId: String): Result<String>
    suspend fun editUserLinkedGmailAccount(userId: String, linkedGmailAccount: String): Result<Boolean>
    suspend fun getUserGmailAppPassword(userId: String): Result<String>
    suspend fun editUserGmailAppPassword(userId: String, gmailAppPassword: String): Result<Boolean>
    suspend fun getUserName(userId: String): Result<String>
    suspend fun getUserEmail(userId: String): Result<String>
    suspend fun getUserCity(userId: String): Result<String>
    suspend fun getUserPhone(userId: String): Result<String>
    suspend fun updateCityPhone(userId: String, cityName: String?, phone: String?): Result<Boolean>
    suspend fun getSkills(userId: String): Result<List<String>>
    suspend fun addSkill(userId: String, skill: String): Result<Boolean>
    suspend fun deleteSkill(userId: String, skill: String): Result<Boolean>
    suspend fun getUserLinkedIn(userId: String): Result<String>
    suspend fun getUserGithub(userId: String): Result<String>
    suspend fun getUserPersonalWebsite(userId: String): Result<String>
    suspend fun updateUserLinks(
        userId: String,
        linkedinUrl: String?,
        githubUrl: String?,
        personalWebsiteUrl: String?
    ): Result<Boolean>
    suspend fun getEducation(userId: String): Result<List<EducationWithDegreeName>>
    suspend fun addEducation(
        userId: String,
        degreeId: Int,
        major: String,
        gpa: Float?,
        startDate: LocalDate,
        endDate: LocalDate,
        institutionName: String
    ): Result<Boolean>
    suspend fun updateEducation(
        userId: String,
        educationID: String,
        degreeId: Int,
        major: String,
        gpa: Float?,
        startDate: LocalDate,
        endDate: LocalDate,
        institutionName: String
    ): Result<Boolean>
    suspend fun deleteEducation(educationID: String): Result<Boolean>
    suspend fun getWorkExperience(userId: String): Result<List<WorkExperience>>
    suspend fun addWorkExperience(
        userId: String,
        companyName: String,
        currentlyWorking: Boolean,
        title: String,
        startDate: LocalDate,
        endDate: LocalDate,
        description: String?
    ): Result<Boolean>
    suspend fun updateWorkExperience(
        userId: String,
        workExperienceID: String,
        companyName: String,
        currentlyWorking: Boolean,
        title: String,
        startDate: LocalDate,
        endDate: LocalDate,
        description: String?
    ): Result<Boolean>
    suspend fun deleteWorkExperience(workExperienceID: String): Result<Boolean>
    suspend fun getDegreeNameById(degreeId: Int): Result<String>
    suspend fun getDegreeIdByName(degreeName: String): Result<Int>
    suspend fun getProjects(userId: String): Result<List<PersonalProject>>
    suspend fun addProject(userId: String, projectName: String, description: String?): Result<Boolean>
    suspend fun updateProject(
        userId: String,
        projectID: String,
        projectName: String,
        description: String?
    ): Result<Boolean>
    suspend fun deleteProject(projectID: String): Result<Boolean>
}
