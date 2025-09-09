package persistence

import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.PersonalProject
import ca.uwaterloo.model.WorkExperience
import ca.uwaterloo.persistence.AuthRepository
import ca.uwaterloo.persistence.UserProfileRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import kotlin.test.*

class UserProfileRepositoryTest {

    private lateinit var userProfileRepository: UserProfileRepository

    @BeforeEach
    fun setUp() {
        println("Setup is running...")
        try {
            val supabaseClient = createSupabaseClient(
                supabaseUrl = "https://gwnlngyvkxdpodenpyyj.supabase.co",
                supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd3bmxuZ3l2a3hkcG9kZW5weXlqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjc5MTAxNTEsImV4cCI6MjA0MzQ4NjE1MX0.olncAUMxSOjcr0YjssWXThtXDXC3q4zasdNYdwavt8g"
            ) {
                install(Postgrest)
                install(Auth)
                install(Storage)
            }

            userProfileRepository = UserProfileRepository(supabaseClient)
        } catch (e: Exception) {
            throw RuntimeException("Failed to initialize Supabase client: ${e.message}", e)
        }
    }

    //test get user's linked gmail account
    @Test
    fun `test get user's linked gmail account`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val result = userProfileRepository.getUserLinkedGmailAccount(userId)
        assertTrue(result.isSuccess, "Get user's linked gmail account should be successful")
        val linkedGmailAccount = result.getOrNull()
        val expectedGmailAccount = "wrw040613@gmail.com"
        assertEquals(expectedGmailAccount, linkedGmailAccount, "Linked gmail account should match the expected value")
    }

    //test edit user's linked gmail account
    @Test
    fun `test edit user's linked gmail account`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val newGmailAccount = "wrw040613@gmail.com"
        val result = userProfileRepository.editUserLinkedGmailAccount(userId, newGmailAccount)
        assertTrue(result.isSuccess, "Edit user's linked gmail account should be successful")
        val updatedGmailAccount = userProfileRepository.getUserLinkedGmailAccount(userId).getOrNull()
        assertEquals(newGmailAccount, updatedGmailAccount, "Linked gmail account should be updated to the new value")
    }

    //test get user's app password
    @Test
    fun `test get user's app password`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val result = userProfileRepository.getUserGmailAppPassword(userId)
        assertTrue(result.isSuccess, "Get user's app password should be successful")
        val appPassword = result.getOrNull()
        val expectedAppPassword = "ekug zqtz pwaf hgsx"
        assertEquals(expectedAppPassword, appPassword, "App password should match the expected value")
    }

    //test edit user's app password
    @Test
    fun `test edit user's app password`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val newAppPassword = "ekug zqtz pwaf hgsx"
        val result = userProfileRepository.editUserGmailAppPassword(userId, newAppPassword)
        assertTrue(result.isSuccess, "Edit user's app password should be successful")
        val updatedAppPassword = userProfileRepository.getUserGmailAppPassword(userId).getOrNull()
        assertEquals(newAppPassword, updatedAppPassword, "App password should be updated to the new value")
    }

    //test get user's name
    @Test
    fun `test get user's name`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val result = userProfileRepository.getUserName(userId)
        assertTrue(result.isSuccess, "Get user's name should be successful")
        val name = result.getOrNull()
        val expectedName = "Cherry Wang"
        assertEquals(expectedName, name, "User's name should match the expected value")
    }

    //test get user's email
    @Test
    fun `test get user's email`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val result = userProfileRepository.getUserEmail(userId)
        assertTrue(result.isSuccess, "Get user's email should be successful")
        val email = result.getOrNull()
        val expectedEmail = "wrw040613@gmail.com"
        assertEquals(expectedEmail, email, "User's email should match the expected value")
    }

    //test get user's city
    @Test
    fun `test get user's city`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val result = userProfileRepository.getUserCity(userId)
        assertTrue(result.isSuccess, "Get user's city should be successful")
        val city = result.getOrNull()
        val expectedCity = "Waterloo"
        assertEquals(expectedCity, city, "User's city should match the expected value")
    }

    //test get user's phone
    @Test
    fun `test get user's phone`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val result = userProfileRepository.getUserPhone(userId)
        assertTrue(result.isSuccess, "Get user's phone should be successful")
        val phone = result.getOrNull()
        val expectedPhone = "2267893910"
        assertEquals(expectedPhone, phone, "User's phone should match the expected value")
    }

    //test update city and phone
    @Test
    fun `test update city and phone`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val newCity = "Waterloo"
        val newPhone = "2267893910"
        val result = userProfileRepository.updateCityPhone(userId, newCity, newPhone)
        assertTrue(result.isSuccess, "Update city and phone should be successful")
        val updatedCity = userProfileRepository.getUserCity(userId).getOrNull()
        val updatedPhone = userProfileRepository.getUserPhone(userId).getOrNull()
        assertEquals(newCity, updatedCity, "City should be updated to the new value")
        assertEquals(newPhone, updatedPhone, "Phone should be updated to the new value")
    }

    //test get user's skills
    @Test
    fun `test get user's skills`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val result = userProfileRepository.getSkills(userId)
        assertTrue(result.isSuccess, "Get user's skills should be successful")
        val skills = result.getOrNull()
        val expectedSkills = listOf("Python", "Java", "C++", "Kotlin", "communication")
        assertEquals(expectedSkills, skills, "User's skills should match the expected value")
    }

    //test add skill
    @Test
    fun `test add skill`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val skill = "Kotlin"
        val result = userProfileRepository.addSkill(userId, skill)
        assertTrue(result.isSuccess, "Add skill should be successful")
        val updatedSkills = userProfileRepository.getSkills(userId).getOrNull()
        val expectedSkills = listOf("Python", "Java", "C++", "Kotlin", "communication")
        assertEquals(expectedSkills, updatedSkills, "User's skills should be updated with the new skill")
    }


    //test get user's LinkedIn
    @Test
    fun `test get user's LinkedIn`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val result = userProfileRepository.getUserLinkedIn(userId)
        assertTrue(result.isSuccess, "Get user's LinkedIn should be successful")
        val linkedinUrl = result.getOrNull()
        val expectedLinkedinUrl = "https://www.linkedin.com/in/cherry"
        assertEquals(expectedLinkedinUrl, linkedinUrl, "User's LinkedIn should match the expected value")
    }

    //test get user's Github
    @Test
    fun `test get user's Github`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val result = userProfileRepository.getUserGithub(userId)
        assertTrue(result.isSuccess, "Get user's Github should be successful")
        val githubUrl = result.getOrNull()
        val expectedGithubUrl = "https://uwaterloo.ca/the-centre/quest"
        assertEquals(expectedGithubUrl, githubUrl, "User's Github should match the expected value")
    }

    //test get user's personal website
    @Test
    fun `test get user's personal website`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val result = userProfileRepository.getUserPersonalWebsite(userId)
        assertTrue(result.isSuccess, "Get user's personal website should be successful")
        val personalWebsiteUrl = result.getOrNull()
        val expectedPersonalWebsiteUrl = "https://cherry.com"
        assertEquals(expectedPersonalWebsiteUrl, personalWebsiteUrl, "User's personal website should match the expected value")
    }

    //test update user's links
    @Test
    fun `test update user's links`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val linkedinUrl = "https://www.linkedin.com/in/cherry"
        val githubUrl = "https://uwaterloo.ca/the-centre/quest"
        val personalWebsiteUrl = "https://cherry.com"
        val result = userProfileRepository.updateUserLinks(userId, linkedinUrl, githubUrl, personalWebsiteUrl)
        assertTrue(result.isSuccess, "Update user's links should be successful")
        val updatedLinkedinUrl = userProfileRepository.getUserLinkedIn(userId).getOrNull()
        val updatedGithubUrl = userProfileRepository.getUserGithub(userId).getOrNull()
        val updatedPersonalWebsiteUrl = userProfileRepository.getUserPersonalWebsite(userId).getOrNull()
        assertEquals(linkedinUrl, updatedLinkedinUrl, "User's LinkedIn should be updated to the new value")
        assertEquals(githubUrl, updatedGithubUrl, "User's Github should be updated to the new value")
        assertEquals(personalWebsiteUrl, updatedPersonalWebsiteUrl, "User's personal website should be updated to the new value")
    }

    //test get user's education
    @Test
    fun `test get user's education`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val result = userProfileRepository.getEducation(userId)
        assertTrue(result.isSuccess, "Get user's education should be successful")
        val education = result.getOrNull()
        val expectedEducation = listOf(
            EducationWithDegreeName(
                id = 34,
                userId = userId,
                degreeName = "Bachelor's Degree",
                major = "Computer Science",
                gpa = 5f,
                startDate = LocalDate.parse("2022-09-01"),
                endDate = LocalDate.parse("2027-06-01"),
                institutionName = "University of Waterloo"
            ),
            EducationWithDegreeName(
                id = 19,
                userId = userId,
                degreeName = "High School Diploma/GED",
                major = "Education",
                gpa = 98f,
                startDate = LocalDate.parse("2019-09-01"),
                endDate = LocalDate.parse("2022-06-01"),
                institutionName = "NJYZ"
            )
        )
        assertEquals(expectedEducation, education, "User's education should match the expected value")
    }

    //test update education
    @Test
    fun `test update education`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val educationId = "34"
        val degreeId = 3
        val major = "Computer Science"
        val gpa = 5f
        val startDate = LocalDate.parse("2022-09-01")
        val endDate = LocalDate.parse("2027-06-01")
        val institutionName = "University of Waterloo"
        val result = userProfileRepository.updateEducation(userId, educationId, degreeId, major, gpa, startDate, endDate, institutionName)
        assertTrue(result.isSuccess, "Update education should be successful")
        val updatedEducation = userProfileRepository.getEducation(userId).getOrNull()
        val expectedEducation = listOf(
            EducationWithDegreeName(
                id = 34,
                userId = userId,
                degreeName = "Bachelor's Degree",
                major = "Computer Science",
                gpa = 5f,
                startDate = LocalDate.parse("2022-09-01"),
                endDate = LocalDate.parse("2027-06-01"),
                institutionName = "University of Waterloo"
            ),
            EducationWithDegreeName(
                id = 19,
                userId = userId,
                degreeName = "High School Diploma/GED",
                major = "Education",
                gpa = 98f,
                startDate = LocalDate.parse("2019-09-01"),
                endDate = LocalDate.parse("2022-06-01"),
                institutionName = "NJYZ"
            )
        )
        assertEquals(expectedEducation, updatedEducation, "User's education should be updated with the new education")
    }


    //test get user's work experience
    @Test
    fun `test get user's work experience`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val result = userProfileRepository.getWorkExperience(userId)
        assertTrue(result.isSuccess, "Get user's work experience should be successful")
        val workExperience = result.getOrNull()
        val expectedWorkExperience = listOf(
            WorkExperience(
                id = 10,
                userId = userId,
                companyName = "Microsoft",
                title = "Software Engineer",
                currentlyWorking = false,
                startDate = LocalDate.parse("2024-01-01"),
                endDate = LocalDate.parse("2024-10-01"),
                description = "Worked on frontend."
            ),
            WorkExperience(
                id = 1,
                userId = userId,
                companyName = "Google",
                title = "Software Engineer",
                currentlyWorking = false,
                startDate = LocalDate.parse("2019-09-01"),
                endDate = LocalDate.parse("2023-06-01"),
                description = "Worked on Android development."
            )

        )
        assertEquals(expectedWorkExperience, workExperience, "User's work experience should match the expected value")
    }


    //test update work experience
    @Test
    fun `test update work experience`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val workExperienceId = "1"
        val companyName = "Google"
        val title = "Software Engineer"
        val currentlyWorking = false
        val startDate = LocalDate.parse("2019-09-01")
        val endDate = LocalDate.parse("2023-06-01")
        val description = "Worked on Android development."
        val result = userProfileRepository.updateWorkExperience(userId, workExperienceId, companyName, currentlyWorking, title, startDate, endDate, description)
        assertTrue(result.isSuccess, "Update work experience should be successful")
        val updatedWorkExperience = userProfileRepository.getWorkExperience(userId).getOrNull()
        val expectedWorkExperience = listOf(
            WorkExperience(
                id = 10,
                userId = userId,
                companyName = "Microsoft",
                title = "Software Engineer",
                currentlyWorking = false,
                startDate = LocalDate.parse("2024-01-01"),
                endDate = LocalDate.parse("2024-10-01"),
                description = "Worked on frontend."
            ),
            WorkExperience(
                id = 1,
                userId = userId,
                companyName = "Google",
                title = "Software Engineer",
                currentlyWorking = false,
                startDate = LocalDate.parse("2019-09-01"),
                endDate = LocalDate.parse("2023-06-01"),
                description = "Worked on Android development."
            )
        )
        assertEquals(expectedWorkExperience, updatedWorkExperience, "User's work experience should be updated with the new work experience")
    }


    //test get user's projects
    @Test
    fun `test get user's projects`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val result = userProfileRepository.getProjects(userId)
        assertTrue(result.isSuccess, "Get user's projects should be successful")
        val projects = result.getOrNull()
        val expectedProjects = listOf(
            PersonalProject(
                id = 13,
                userId = userId,
                projectName = "Snowmail",
                description = "ssss"
            )
        )
        assertEquals(expectedProjects, projects, "User's projects should match the expected value")
    }


    //test update project
    @Test
    fun `test update project`(): Unit = runBlocking {
        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
        val projectId = "13"
        val projectName = "Snowmail"
        val description = "ssss"
        val result = userProfileRepository.updateProject(userId, projectId, projectName, description)
        assertTrue(result.isSuccess, "Update project should be successful")
        val updatedProjects = userProfileRepository.getProjects(userId).getOrNull()
        val expectedProjects = listOf(
            PersonalProject(
                id = 13,
                userId = userId,
                projectName = "Snowmail",
                description = "ssss"
            )
        )
        assertEquals(expectedProjects, updatedProjects, "User's projects should be updated with the new project")
    }
}