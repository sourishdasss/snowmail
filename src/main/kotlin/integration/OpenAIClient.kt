package integration

import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.PersonalProject
import ca.uwaterloo.model.WorkExperience
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import io.ktor.http.*
import kotlinx.serialization.json.Json
import model.UserInput
import model.OpenAIRequest
import model.UserProfile

// Data classes for OpenAI API
@Serializable
data class ChatCompletionResponse(
    val id: String,
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message
)

@Serializable
data class Message(
    val content: String
)


class OpenAIClient(private val httpClient: HttpClient) {

    // Generate an email from a specified resume
    suspend fun generateEmailFromResume(userInput: UserInput, userProfile: UserProfile, resumeText:String): String? {
        val prompt = buildPromptFromResume(userInput, userProfile, resumeText)
        val message = prepareMessage(prompt)
        val response = sendOpenAIRequest(message)
        val emailContent = getEmailContent(response)
        return emailContent
    }

    // Generate an email from a user's profile
    suspend fun generateEmailFromProfile(userInput: UserInput, userProfile: UserProfile, education: List<EducationWithDegreeName>, workExperience: List<WorkExperience>, projects: List<PersonalProject>, skills: List<String>): String? {
        val prompt = buildPromptFromProfile(userInput, userProfile, education, workExperience, projects, skills)
        val message = prepareMessage(prompt)
        val response = sendOpenAIRequest(message)
        val emailContent = getEmailContent(response)
        return emailContent
    }

    // Build an LLM prompt for generating an email from a resume
    private fun buildPromptFromResume(userInput: UserInput, userProfile: UserProfile, resumeText: String, ): String {
        val companyName = userInput.company
        val jobDescription = userInput.jobDescription
        val recruiterName = userInput.recruiterName

        return """
            The company I am looking to apply to is $companyName, with the following job description: $jobDescription.
            
            Here is my resume:
            $resumeText
            
            Send a job application email to the recruiter, $recruiterName, that is personalized, formal, and aligned with my profile, skills, and experience as they relate to the job description provided. 
            Try to highlight the most relevant skills and experiences from my resume that match the job description, and if the job description is vague, focus on general skills and experiences that are applicable to the role.
            Make sure to keep the email professional and concise, avoiding overly casual language or excessive detail.
        """.trimIndent()
    }

    // Build an LLM prompt for generating an email from a user's profile
    private fun buildPromptFromProfile(userInput: UserInput, userProfile: UserProfile, education: List<EducationWithDegreeName>, workExperience: List<WorkExperience>, projects: List<PersonalProject>, skills: List<String>): String {
        val companyName = userInput.company
        val jobDescription = userInput.jobDescription
        val recruiterName = userInput.recruiterName

        val userSkills = skills?.joinToString(", ") ?: "Not provided"

        val educationDetails = education.joinToString("\n") { e ->
            """
            - Institution: ${e.institutionName}
            - Degree: ${e.major}
            - GPA: ${e.gpa ?: "Not provided"}
            - End Date: ${e.endDate}
            """.trimIndent()
        }

        val workExperienceDetails = workExperience.joinToString("\n") { w ->
            """
            - Company: ${w.companyName}
            - Position: ${w.title}
            - Description: ${w.description}
            """.trimIndent()
        }

        val projectDetails = projects.joinToString("\n") { p ->
            """
            - Project Name: ${p.projectName}
            - Description: ${p.description}
            """.trimIndent()
        }

        return """
            The company I am looking to apply to is $companyName, with the following job description: $jobDescription.
            
            Here is my resume overview:
            First Name: ${userProfile.firstName}
            Last Name: ${userProfile.lastName}
            Skills: $userSkills
            Education:
            $educationDetails
            Work Experience:
            $workExperienceDetails
            Personal Projects:
            $projectDetails
            
            Send a job application email to the recruiter, $recruiterName, that is personalized, formal, and aligned with my profile, skills, and experience as they relate to the job description provided. 
        """.trimIndent()
    }

    // Prepare the message to send to the OpenAI API
    private fun prepareMessage(prompt: String): List<Map<String, String>> {
        return listOf(
            mapOf(
                "role" to "system",
                "content" to """
                    You are a professional email generator that creates highly effective job application emails. 
                    The emails should be personalized, formal, and aligned with the user's profile, skills, and experience as they relate to the job description provided. 
                    Ensure the email includes:
                    - A courteous and professional greeting
                    - A brief introduction of the applicant
                    - Highlights of relevant skills and experiences tailored to the job description
                    - A clear, polite call to action for follow-up
                    - A welcome with "Hi" or "Hello", followed up the recruiter's name 
                    - Always specify the subject with "Subject:" followed by the subject of the email
                    - A formal closing with the applicant's name and something like "Best regards" or "Sincerely"
                    - Keep the tone professional and succinct, avoiding overly casual language or excessive detail.
                    
                    Real Example of a Job Application Email:
                    Subject: Software Engineer Internship Opportunities at Coinbase
                    
                    Hi Jane,

                    I hope this message finds you well. My name is John Doe, a third-year Computer Science student at the University of Waterloo, and I’m excited about the innovative work happening at Coinbase. With my background in data engineering and software development, I’m confident I can contribute meaningfully to your team.
                    Here’s a quick snapshot of my relevant experience:

                    - Manulife: As a Data Engineer Intern, I worked on optimizing CI/CD pipelines, automated GitHub  repo access management, and implemented scalable solutions for database migrations and cloud deployment.
                    - Baraka (YC ’21): As a Software Engineer Intern, I developed backend systems, deployed scalable solutions, and built efficient ETL pipelines for financial data processing.

                    I would love to discuss your team’s current challenges and explore how I can help solve them. I'm happy to volunteer my time to demonstrate the value I can bring. Would you be available for a brief 15-minute conversation this week?
                    I have attached my resume, looking forward to hearing from you.

                    Best regards,
                    John Doe
                    
                """.trimIndent()
            ),
            mapOf("role" to "user", "content" to prompt))
    }

    // Send a request to the OpenAI API
    private suspend fun sendOpenAIRequest(message: List<Map<String, String>>): HttpResponse {
        val request = OpenAIRequest(
            model = "gpt-3.5-turbo",
            messages = message,
            max_tokens = 500
        )

        return try {
            httpClient.post("https://api.openai.com/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer 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")
                setBody(Json.encodeToString(OpenAIRequest.serializer(), request))
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to call OpenAI API: ${e.message}")
        }
    }

    // Extract the email content from the OpenAI API response
    private suspend fun getEmailContent(response: HttpResponse): String? {
        val responseBody: String = response.bodyAsText()

        val json = Json {
            ignoreUnknownKeys = true
        }

        return try {
            val parsedResponse = json.decodeFromString(ChatCompletionResponse.serializer(), responseBody)
            parsedResponse.choices.firstOrNull()?.message?.content
        } catch (e: Exception) {
            println("Failed to parse content: ${e.message}")
            null
        }
    }

    // Parse the resume to extract relevant details
    suspend fun parseResume(resumeText: String): Map<String, Any> {
        val prompt = """
            Extract the following details from the resume:
            - Name
            - Email
            - Phone
            - Education
            - Work Experience
            - Skills
    
            Resume:
            $resumeText
        """.trimIndent()

        val message = listOf(
            mapOf("role" to "user", "content" to prompt)
        )

        val response = sendOpenAIRequest(message)

        val responseBody: String = response.bodyAsText()
        println("OpenAI API response: $responseBody")

        val json = Json { ignoreUnknownKeys = true }
        return try {
            val parsedResponse = json.decodeFromString(ChatCompletionResponse.serializer(), responseBody)
            val extractedText = parsedResponse.choices.firstOrNull()?.message?.content ?: "No data extracted"

            // Parse the extracted text to create a user profile object
            val userProfileDetails = mutableMapOf<String, Any>()
            val lines = extractedText.split("\n")
            for (line in lines) {
                val parts = line.split(":")
                if (parts.size == 2) {
                    userProfileDetails[parts[0].trim()] = parts[1].trim()
                }
            }
            userProfileDetails
        } catch (e: Exception) {
            println("Failed to parse response: ${e.message}")
            throw RuntimeException("Failed to parse response: ${e.message}")
        }
    }

    // Determines the status of the application
    suspend fun classifyStatusOfApplication(emailContent: String): String {
        val prompt = """
            Determine the application status based on the email content. The status can be one of the following:
            - APPLIED
            - INTERVIEWING
            - OFFER
            - OTHER
            - REJECTED
            
            You can only reply with one of the five words listed above based on the email content, and make sure it is uppercase.
            If the email content is inconclusive, you can reply with "OTHER".
    
            Email content:
            $emailContent
        """.trimIndent()

        val message = listOf(
            mapOf("role" to "user", "content" to prompt)
        )

        val response = sendOpenAIRequest(message)
        return getEmailContent(response)?.trim() ?: "Unknown"
    }
}
