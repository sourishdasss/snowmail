package ca.uwaterloo.controller

import integration.OpenAIClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

import java.nio.file.Files
import java.nio.file.Paths
import kotlinx.coroutines.runBlocking
import java.io.File

class ResumeParserController(private val openAIClient: OpenAIClient) {

    // Parse a resume file with LLM and return the extracted information
    suspend fun parseResume(fileBytes: ByteArray): Map<String, Any> {
        val resumeText = String(fileBytes)
        return openAIClient.parseResume(resumeText)
    }

    // Extracts text from a PDF file
    fun extractTextFromPDF(file: File): String {
        PDDocument.load(file).use { document ->
            return PDFTextStripper().getText(document)
        }
    }
}
