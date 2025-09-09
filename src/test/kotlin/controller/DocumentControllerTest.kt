import ca.uwaterloo.controller.DocumentController
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font

class DocumentControllerTest {

    private val dbStorage = SupabaseClient()
    private val documentController = DocumentController(dbStorage.documentRepository)

    @Test
    fun testUploadDocument() = runBlocking {
        val tempFile = File.createTempFile("test-file", ".pdf")

        // Create a PDF document
        PDDocument().use { document ->
            val page = PDPage()
            document.addPage(page)

            PDPageContentStream(document, page).use { contentStream ->
                contentStream.beginText()
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12f)
                contentStream.newLineAtOffset(100f, 700f)
                contentStream.showText("Testing Supabase!")
                contentStream.endText()
            }

            document.save(tempFile)
        }

        val bucket = "user_documents"
        val userId = "test"
        val documentType = "other"
        val documentName = "testing-file.pdf"

        val result = documentController.uploadDocument(bucket, userId, documentType, documentName, tempFile)
        assertTrue(result.isSuccess, "Document upload failed: ${result.exceptionOrNull()?.message}")
    }

    @Test
    fun testDownloadDocument() = runBlocking {
        val bucket = "user_documents"
        val userId = "test"
        val documentType = "other"
        val documentName = "testing-file.pdf"

        // Attempt to download the document
        val downloadResult = documentController.downloadDocument(bucket, userId, documentType, documentName)
        assertTrue(downloadResult.isSuccess, "Document download failed: ${downloadResult.exceptionOrNull()?.message}")
        assertNotNull(downloadResult.getOrNull(), "Downloaded document content is null")

        // Save the downloaded content to a file
        val downloadedFile = File.createTempFile("downloaded-file", ".pdf")
        downloadedFile.writeBytes(downloadResult.getOrNull()!!)
        assertTrue(downloadedFile.exists(), "Downloaded file does not exist")
    }

    @Test
    fun testDeleteDocument() = runBlocking {
        val bucket = "user_documents"
        val userId = "test"
        val documentType = "other"
        val documentName = "testing-file.pdf"

        // Delete the document
        val deleteResult = documentController.deleteDocument(bucket, userId, documentType, documentName)
        assertTrue(deleteResult.isSuccess, "Document deletion failed: ${deleteResult.exceptionOrNull()?.message}")

        // Verify the document is deleted
        val downloadResult = documentController.downloadDocument(bucket, userId, documentType, documentName)
        assertTrue(!downloadResult.isFailure, "Document still exists after deletion.")
    }
}
