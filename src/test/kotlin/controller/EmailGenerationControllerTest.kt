package controller

import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.PersonalProject
import ca.uwaterloo.model.WorkExperience
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import model.GeneratedEmail
import model.UserInput
import model.UserProfile
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import service.EmailGenerationService
import java.io.File

class EmailGenerationControllerTest {

    private val emailGenerationService = mockk<EmailGenerationService>()
    private val emailGenerationController = EmailGenerationController(emailGenerationService)

    private fun createTempPdfFile(): File {
        val tempFile = File.createTempFile("resume", ".pdf")
        tempFile.writeText("Sample resume content")
        return tempFile
    }

    @Test
    fun `test generateEmail with resume`() = runBlocking {
        val userInput = UserInput(
            jobDescription = "test job description",
            jobTitle = "test job title",
            company = "test company",
            recruiterName = "test recruiter",
            recruiterEmail = "test@example.com",
            fileURLs = null
        )
        val userProfile = UserProfile(
            userId = "123",
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com"
        )
        val resumeFile = createTempPdfFile()
        val generatedEmail = GeneratedEmail("Generated email subject", "Generated email body")

        coEvery { emailGenerationService.generateEmailFromResume(userInput, userProfile, resumeFile) } returns generatedEmail

        val result = emailGenerationController.generateEmail("resume", userInput, userProfile, resumeFile = resumeFile)

        assertNotNull(result)
        assertEquals(generatedEmail, result)
    }

    @Test
    fun `test generateEmail with profile`() = runBlocking {
        val userInput = UserInput(
            jobDescription = "Building scalable systems",
            jobTitle = "Software Engineer",
            company = "Google",
            recruiterName = "Jane Doe",
            recruiterEmail = "jane.doe@example.com",
            fileURLs = null
        )
        val userProfile = UserProfile(
            userId = "123",
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@gmail.com"
        )
        val education = listOf(
            EducationWithDegreeName(
                id = 1,
                userId = "123",
                degreeName = "Bachelor's",
                institutionName = "University of Waterloo",
                major = "Computer Science",
                gpa = 4.0f,
                startDate = LocalDate.parse("2019-09-01"),
                endDate = LocalDate.parse("2023-06-01")
            )
        )
        val workExperience = listOf(
            WorkExperience(
                userId = "123",
                companyName = "Company",
                currentlyWorking = false,
                title = "Title",
                startDate = LocalDate.parse("2020-01-01"),
                endDate = LocalDate.parse("2021-01-01"),
                description = "Description"
            )
        )
        val projects = listOf(
            PersonalProject(
                userId = "123",
                projectName = "Project",
                description = "Description"
            )
        )
        val skills = listOf("Kotlin", "Java")
        val generatedEmail = GeneratedEmail("Generated email subject", "Generated email body")

        coEvery { emailGenerationService.generateEmailFromProfile(userInput, userProfile, education, workExperience, projects, skills) } returns generatedEmail

        val result = emailGenerationController.generateEmail("profile", userInput, userProfile, education, workExperience, projects, skills)

        assertNotNull(result)
        assertEquals(generatedEmail, result)
    }
}
