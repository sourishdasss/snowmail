package ca.uwaterloo.service

import integration.OpenAIClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

import java.nio.file.Paths
import kotlinx.coroutines.runBlocking
import model.GeneratedEmail
import model.UserInput
import java.io.File

class ParserService(private val openAIClient: OpenAIClient) {

    // Function to parse resume without HTTP endpoint
    suspend fun parseResume(fileBytes: ByteArray): Map<String, Any> {
        val resumeText = String(fileBytes)
        return openAIClient.parseResume(resumeText)
    }

    fun extractTextFromPDF(file: File): String {
        println("entered extractTextFromPDF")
        PDDocument.load(file).use { document ->
            println("entered PDDocument.load")
            return PDFTextStripper().getText(document)
        }
    }

    fun parseEmailContent(emailText: String?): GeneratedEmail? {
        val lines = emailText?.lines()
        val subjectLine = lines?.firstOrNull { it.startsWith("Subject:") } ?: ""
        val subject = subjectLine.removePrefix("Subject:").trim()
        val greetings = listOf("Dear", "Hi", "Hello", "Greetings", "To Whom It May Concern")
        val body = lines?.dropWhile { line -> greetings.none { line.startsWith(it) } }?.joinToString("\n")?.trim()
        return body?.let { GeneratedEmail(subject, it) }
    }
}

suspend fun main() {
    val openAIClient = OpenAIClient(HttpClient(CIO))
    val resumeParserController = ParserService(openAIClient)

    // Read the resume file
    // val path = Paths.get("src/test/resources/test-resume.pdf")

    val path = Paths.get(System.getProperty("user.home") + "/Desktop/resume-external.pdf")
    val file = path.toFile()

    // Extract text from the PDF
//    val resumeText = resumeParserController.extractTextFromPDF(file)
//
//    println(resumeText)
//
//    val userInput = UserInput(
//        jobDescription = "Software Engineer",
//        recruiterEmail = "recruiter@example.com",
//        jobTitle = "Software Engineer",
//        company = "Example Corp",
//        recruiterName = "Jane Doe",
//        fileURLs = listOf("https://example.com/resume.pdf"),
//    )


    fun getResourceFilePath(resourceName: String): String {
        val resource = ParserService::class.java.classLoader.getResource(resourceName)
        requireNotNull(resource) { "Resource not found: $resourceName" }
        return Paths.get(resource.toURI()).toString()
    }

    // Usage example
    val filePath = getResourceFilePath("test-resume.pdf")
    println("Resource file path: $filePath")

}
