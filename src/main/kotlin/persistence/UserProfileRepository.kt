package ca.uwaterloo.persistence

import ca.uwaterloo.model.Education
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.PersonalProject
import ca.uwaterloo.model.WorkExperience
import model.UserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import java.util.*

class UserProfileRepository(private val supabase: SupabaseClient) : IUserProfileRepository{

    // get user's linked gmail account
    override suspend fun getUserLinkedGmailAccount(userId: String): Result<String> {
        return try {
            // fetch user's linked gmail account from db based on userid
            val linkedGmailAccount = supabase.from("user_profile")
                .select(columns = Columns.list("linked_gmail_account")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<Map<String, String?>>()

            val gmailAccount = linkedGmailAccount["linked_gmail_account"] ?: ""
            Result.success(gmailAccount)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch linked gmail account: ${e.message}"))
        }
    }

    // edit user's linked gmail account
    override suspend fun editUserLinkedGmailAccount(userId: String, linkedGmailAccount: String): Result<Boolean> {
        return try {
            // Check if linkedGmailAccount ends with "@gmail.com"
            if (!linkedGmailAccount.endsWith("@gmail.com")) {
                throw IllegalArgumentException("Invalid Gmail account: must end with '@gmail.com'")
            }

            withContext(Dispatchers.IO) {
                supabase.from("user_profile")
                    .update(mapOf("linked_gmail_account" to linkedGmailAccount)){
                        filter {
                            eq("user_id", userId)
                        }
                    }
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update linked gmail account: ${e.message}"))
        }
    }

    // get user's gmail app password
    override suspend fun getUserGmailAppPassword(userId: String): Result<String> {
        return try {
            // fetch user's gmail app password from db based on userid
            val gmailAppPassword = supabase.from("user_profile")
                .select(columns = Columns.list("gmail_app_password")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<Map<String, String?>>()

            val appPassword = gmailAppPassword["gmail_app_password"] ?: ""
            Result.success(appPassword)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch gmail app password: ${e.message}"))
        }
    }

    // edit user's gmail app password
    override suspend fun editUserGmailAppPassword(userId: String, gmailAppPassword: String): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                supabase.from("user_profile")
                    .update(mapOf("gmail_app_password" to gmailAppPassword)){
                        filter {
                            eq("user_id", userId)
                        }
                    }
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update gmail app password: ${e.message}"))
        }
    }

    override suspend fun getUserName(userId: String): Result<String> {
        return try {
            // fetch user's name from db based on userid
            val userProfile = supabase.from("user_profile")
                .select(columns = Columns.list("user_id", "first_name", "last_name")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<UserProfile>()

            Result.success("${userProfile.firstName} ${userProfile.lastName}")
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch profile: ${e.message}"))
        }
    }

    override suspend fun getUserEmail(userId: String): Result<String> {
        return try {
            // fetch user's email from db based on userid
            val emailResult = supabase.from("user_profile")
                .select(columns = Columns.list("email")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<Map<String, String>>()

            val email = emailResult["email"] ?: throw Exception("Email not found")
            Result.success(email)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch profile: ${e.message}"))
        }
    }

    override suspend fun getUserCity(userId: String): Result<String> {
        return try {
            // fetch user's city from db based on userid
            val cityResult = supabase.from("user_profile")
                .select(columns = Columns.list("city_name")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<Map<String, String?>>()

            val city = cityResult["city_name"] ?: ""
            Result.success(city)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch profile: ${e.message}"))
        }
    }

    override suspend fun getUserPhone(userId: String): Result<String> {
        return try {
            // fetch user's phone from db based on userid
            val phoneResult = supabase.from("user_profile")
                .select(columns = Columns.list("phone")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<Map<String, String?>>()

            val phone = phoneResult["phone"] ?: ""
            Result.success(phone)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch profile: ${e.message}"))
        }
    }

    override suspend fun updateCityPhone(userId: String, cityName: String?, phone: String?): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                supabase.from("user_profile")
                    .update(mapOf("city_name" to cityName, "phone" to phone)){
                        filter {
                            eq("user_id", userId)
                        }
                    }
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update user profile: ${e.message}"))
        }
    }

    override suspend fun getSkills(userId: String): Result<List<String>> {
        return try {
            // get user's skills from db based on userid
            val skillsResult = supabase.from("user_skills")
                .select(columns = Columns.list("skill")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Map<String, String>>()

            val skills = skillsResult.mapNotNull { it["skill"] }
            Result.success(skills)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch skills: ${e.message}"))
        }
    }

    override suspend fun addSkill(userId: String, skill: String): Result<Boolean> {
        return try {
            // check if the skill already exists
            val existingSkill = supabase.from("user_skills")
                .select(columns = Columns.list("skill")) {
                    filter {
                        eq("user_id", userId)
                        eq("skill", skill)
                    }
                }
                .decodeList<Map<String, String>>()

            // if the skill already exists, return success
            if (existingSkill.isNotEmpty()) {
                return Result.success(true)
            }

            // insert the skill into the database
            supabase.from("user_skills")
                .insert(mapOf("user_id" to userId, "skill" to skill))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to add skill: ${e.message}"))
        }
    }

    override suspend fun deleteSkill(userId: String, skill: String): Result<Boolean> {
        return try {
            // check if the skill exists
            val existingSkill = supabase.from("user_skills")
                .select(columns = Columns.list("skill")) {
                    filter {
                        eq("user_id", userId)
                        eq("skill", skill)
                    }
                }
                .decodeList<Map<String, String>>()

            // if the skill does not exist, return failure
            if (existingSkill.isEmpty()) {
                return Result.failure(Exception("Skill not found for user: $userId"))
            }

            // delete the skill from the database
            supabase.from("user_skills")
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("skill", skill)
                    }
                }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to delete skill: ${e.message}"))
        }
    }

    override suspend fun getUserLinkedIn (userId: String): Result<String> {
        return try {
            // fetch user's linkedin url from db based on userid
            val linkedinResult = supabase.from("user_profile")
                .select(columns = Columns.list("linkedin")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<Map<String, String?>>()

            val linkedinUrl = linkedinResult["linkedin"] ?: ""
            Result.success(linkedinUrl)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch linkedin URL: ${e.message}"))
        }
    }

    override suspend fun getUserGithub(userId: String): Result<String> {
        return try {
            // fetch user's github url from db based on userid
            val githubResult = supabase.from("user_profile")
                .select(columns = Columns.list("github")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<Map<String, String?>>()

            val githubUrl = githubResult["github"] ?: ""
            Result.success(githubUrl)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch github URL: ${e.message}"))
        }
    }

    override suspend fun getUserPersonalWebsite(userId: String): Result<String> {
        return try {
            // fetch user's personal website url from db based on userid
            val personalWebsiteResult = supabase.from("user_profile")
                .select(columns = Columns.list("personal_web")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<Map<String, String?>>()

            val personalWebsiteUrl = personalWebsiteResult["personal_web"] ?: ""
            Result.success(personalWebsiteUrl)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch personal website URL: ${e.message}"))
        }
    }

    override suspend fun updateUserLinks(
        userId: String,
        linkedinUrl: String?,
        githubUrl: String?,
        personalWebsiteUrl: String?
    ): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                supabase.from("user_profile")
                    .update(mapOf("linkedin" to linkedinUrl, "github" to githubUrl, "personal_web" to personalWebsiteUrl)){
                        filter {
                            eq("user_id", userId)
                        }
                    }
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update user profile: ${e.message}"))
        }
    }

    //get education experiences order by time
    override suspend fun getEducation(userId: String): Result<List<EducationWithDegreeName>> {
        return try {
            // fetch education records from db based on userid
            val educationList = supabase.from("education")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                    order("start_date", Order.DESCENDING)
                }
                .decodeList<Education>()

            // map education records to EducationWithDegreeName objects
            val educationWithNames = educationList.map { education ->
                val degreeNameResult = getDegreeNameById(education.degreeId)
                val degreeName = degreeNameResult.getOrElse {
                    return Result.failure(Exception("Failed to fetch degree name for degree ID ${education.degreeId}: ${it.message}"))
                }

                // return EducationWithDegreeName object
                EducationWithDegreeName(
                    id = education.id,
                    userId = education.userId,
                    degreeName = degreeName,
                    institutionName = education.institutionName,
                    major = education.major,
                    gpa = education.gpa,
                    startDate = education.startDate,
                    endDate = education.endDate
                )
            }
            Result.success(educationWithNames)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch education: ${e.message}"))
        }
    }


    override suspend fun addEducation(
        userId: String,
        degreeId: Int,
        major: String,
        gpa: Float?,
        startDate: LocalDate,
        endDate: LocalDate,
        institutionName: String
    ): Result<Boolean> {
        return try {
            val education = Education(
                userId = userId,
                degreeId = degreeId,
                institutionName = institutionName,
                major = major,
                gpa = gpa,
                startDate = startDate,
                endDate = endDate
            )
            supabase.from("education").insert(education)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to add education: ${e.message}"))
        }
    }

    override suspend fun updateEducation(
        userId: String,
        educationID: String,
        degreeId: Int,
        major: String,
        gpa: Float?,
        startDate: LocalDate,
        endDate: LocalDate,
        institutionName: String
    ): Result<Boolean> {
        return try {
            // check if education exists
            val existingEducation = supabase.from("education")
                .select {
                    filter {
                        eq("id", educationID)
                    }
                }
                .decodeSingleOrNull<Education>()

            if (existingEducation != null) {
                // if exists, update it

                supabase.from("education")
                    .update(mapOf(
                        "degree_id" to degreeId.toString(),
                        "major" to major,
                        "gpa" to gpa.toString(),
                        "start_date" to startDate.toString(),
                        "end_date" to endDate.toString(),
                        "institution_name" to institutionName
                    )) {
                        filter {
                            eq("id", educationID)
                        }
                    }

                Result.success(true)
            } else {
                // if not exists, return failure
                Result.failure(Exception("Education record with ID $educationID not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update education: ${e.message}"))
        }
    }

    override suspend fun deleteEducation(educationID: String): Result<Boolean> {
        return try {
            // check if education exists
            val existingEducation = supabase.from("education")
                .select {
                    filter {
                        eq("id", educationID)
                    }
                }
                .decodeSingleOrNull<Education>()

            if (existingEducation != null) {
                // if exists, delete it
                supabase.from("education")
                    .delete {
                        filter {
                            eq("id", educationID)
                        }
                    }
                Result.success(true)
            } else {
                // if not exists, return failure
                Result.failure(Exception("Education record with ID $educationID not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to delete education: ${e.message}"))
        }
    }


    override suspend fun getWorkExperience(userId: String): Result<List<WorkExperience>> {
        return try {
            val workExperience = supabase.from("work_experience")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                    order("start_date", Order.DESCENDING)
                }
                .decodeList<WorkExperience>()
            Result.success(workExperience)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch work experience: ${e.message}"))
        }
    }

    override suspend fun addWorkExperience(
        userId: String,
        companyName: String,
        currentlyWorking: Boolean,
        title: String,
        startDate: LocalDate,
        endDate: LocalDate,
        description: String?
    ) : Result<Boolean>{
        return try {
            val workExperience = WorkExperience(
                userId = userId,
                companyName = companyName,
                currentlyWorking = currentlyWorking,
                title = title,
                startDate = startDate,
                endDate = endDate,
                description = description
            )
            supabase.from("work_experience").insert(workExperience)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to add working experience: ${e.message}"))
        }
    }

    override suspend fun updateWorkExperience(
        userId: String,
        workExperienceID: String,
        companyName: String,
        currentlyWorking: Boolean,
        title: String,
        startDate: LocalDate,
        endDate: LocalDate,
        description: String?
    ): Result<Boolean> {
        return try {
            // check if work experience exists
            val existingWorkExperience = supabase.from("work_experience")
                .select {
                    filter {
                        eq("id", workExperienceID)
                    }
                }
                .decodeSingleOrNull<WorkExperience>()

            if (existingWorkExperience != null) {
                // if exists, update it
                supabase.from("work_experience")
                    .update(mapOf(
                        "company_name" to companyName,
                        "currently_working" to currentlyWorking.toString(),
                        "title" to title,
                        "start_date" to startDate.toString(),
                        "end_date" to endDate.toString(),
                        "description" to description
                    )) {
                        filter {
                            eq("id", workExperienceID)
                        }
                    }
                Result.success(true)
            } else {
                // if not exists, return failure
                Result.failure(Exception("Work experience with ID $workExperienceID not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update work experience: ${e.message}"))
        }
    }

    override suspend fun deleteWorkExperience(workExperienceID: String): Result<Boolean> {
        return try {
            // check if work experience exists
            val existingWorkExperience = supabase.from("work_experience")
                .select {
                    filter {
                        eq("id", workExperienceID)
                    }
                }
                .decodeSingleOrNull<WorkExperience>()

            if (existingWorkExperience != null) {
                // if exists, delete it
                supabase.from("work_experience")
                    .delete {
                        filter {
                            eq("id", workExperienceID)
                        }
                    }
                Result.success(true)
            } else {
                // if not exists, return failure
                Result.failure(Exception("Work experience with ID $workExperienceID not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to delete work experience: ${e.message}"))
        }
    }

    override suspend fun getDegreeNameById(degreeId: Int): Result<String> {
        return try {
            // fetch degree name from db based on degreeId
            val degreeResult = supabase.from("degree")
                .select(columns = Columns.list("degree_name")) {
                    filter {
                        eq("degree_id", degreeId)
                    }
                }
                .decodeSingleOrNull<Map<String, String>>() ?: throw Exception("Degree not found")

            val degreeName = degreeResult["degree_name"] ?: throw Exception("Degree name not found")
            Result.success(degreeName)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to map degree ID to name: ${e.message}"))
        }
    }


    override suspend fun getDegreeIdByName(degreeName: String): Result<Int> {
        return try {
            // fetch degree id from db based on degreeName
            val degreeResult = supabase.from("degree")
                .select(columns = Columns.list("degree_id")) {
                    filter {
                        eq("degree_name", degreeName)
                    }
                }
                .decodeSingleOrNull<Map<String, Int>>()

            val degreeId = degreeResult?.get("degree_id") ?: throw Exception("Degree ID not found")
            Result.success(degreeId)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to map degree name to ID: ${e.message}"))
        }
    }

    //get user's projects
    override suspend fun getProjects(userId: String): Result<List<PersonalProject>> {
        return try {
            // fetch user's projects from db based on userid
            val projects = supabase.from("personal_project")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<PersonalProject>()
            Result.success(projects)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch projects: ${e.message}"))
        }
    }

    override suspend fun addProject(userId: String, projectName: String, description: String?): Result<Boolean> {
        return try {
            val project = PersonalProject(
                userId = userId,
                projectName = projectName,
                description = description
            )
            supabase.from("personal_project").insert(project)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to add project: ${e.message}"))
        }
    }

    //update user's project
    override suspend fun updateProject(
        userId: String,
        projectID: String,
        projectName: String,
        description: String?
    ): Result<Boolean> {
        return try {
            // check if project exists
            val existingProject = supabase.from("personal_project")
                .select {
                    filter {
                        eq("id", projectID)
                    }
                }
                .decodeSingleOrNull<PersonalProject>()

            if (existingProject != null) {
                // if exists, update it
                supabase.from("personal_project")
                    .update(mapOf(
                        "project_name" to projectName,
                        "description" to description
                    )) {
                        filter {
                            eq("id", projectID)
                        }
                    }
                Result.success(true)
            } else {
                // if not exists, return failure
                Result.failure(Exception("Project with ID $projectID not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update project: ${e.message}"))
        }
    }

    //delete user's project
    override suspend fun deleteProject(projectID: String): Result<Boolean> {
        return try {
            // check if project exists
            val existingProject = supabase.from("personal_project")
                .select {
                    filter {
                        eq("id", projectID)
                    }
                }
                .decodeSingleOrNull<PersonalProject>()

            if (existingProject != null) {
                // if exists, delete it
                supabase.from("personal_project")
                    .delete {
                        filter {
                            eq("id", projectID)
                        }
                    }
                Result.success(true)
            } else {
                // if not exists, return failure
                Result.failure(Exception("Project with ID $projectID not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to delete project: ${e.message}"))
        }
    }


}
