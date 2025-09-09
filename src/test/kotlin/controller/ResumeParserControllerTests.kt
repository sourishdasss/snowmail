package controller

import ca.uwaterloo.controller.ResumeParserController
import integration.OpenAIClient
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class ResumeParserControllerTest {

    private val openAIClient = mockk<OpenAIClient>()
    private val resumeParserController = ResumeParserController(openAIClient)

    private fun createTempPdfFile(content: String): File {
        val tempFile = File.createTempFile("resume", ".pdf")
        PDDocument().use { document ->
            val page = PDPage()
            document.addPage(page)
            PDPageContentStream(document, page).use { contentStream ->
                contentStream.beginText()
                contentStream.setFont(PDType1Font.HELVETICA, 12f)
                contentStream.newLineAtOffset(25f, 500f)
                contentStream.showText(content)
                contentStream.endText()
            }
            document.save(tempFile)
        }
        return tempFile
    }

    @Test
    fun `test parseResume`() = runBlocking {
        val resumeText = "Sample resume content"
        val fileBytes = resumeText.toByteArray()
        val parsedResume = mapOf("name" to "John Doe", "email" to "john.doe@example.com")

        coEvery { openAIClient.parseResume(resumeText) } returns parsedResume

        val result = resumeParserController.parseResume(fileBytes)

        assertNotNull(result)
        assertEquals(parsedResume, result)
    }

    @Test
    fun `test extractTextFromPDF`() {
        val resumeContent = "Sample resume content\n"
        val resumeFile = createTempPdfFile(resumeContent.trim())

        val result = resumeParserController.extractTextFromPDF(resumeFile)

        assertNotNull(result)
        assertEquals(resumeContent, result)
    }
}
