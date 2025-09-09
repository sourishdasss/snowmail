package ca.uwaterloo.controller

import ca.uwaterloo.persistence.IDocumentRepository
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

class DocumentController(private val documentRepository: IDocumentRepository) {

    // Sanitize the document name to remove any invalid characters
    private fun sanitizeDocumentName(documentName: String): String {
        return documentName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }

    // Upload a document to the specified bucket
    suspend fun uploadDocument(bucket: String, userId: String, documentType: String, documentName: String, file: File): Result<String> {
        val sanitizedDocumentName = sanitizeDocumentName(documentName)
        val path = "$userId/$documentType/$sanitizedDocumentName"
        return documentRepository.uploadDocument(bucket, path, file)
    }

    // Download a document from the specified bucket as a ByteArray
    suspend fun downloadDocument(bucket: String, userId: String, documentType: String, documentName: String): Result<ByteArray> {
        val path = "$userId/$documentType/$documentName"
        return documentRepository.downloadDocument(bucket, path)
    }

    // Delete a document from the specified bucket
    suspend fun deleteDocument(bucket: String, userId: String, documentType: String, documentName: String): Result<String> {
        val path = "$userId/$documentType/$documentName"
        return documentRepository.deleteDocument(bucket, path)
    }

    // Create a signed URL to view a document
    suspend fun viewDocument(bucket: String, userId: String, documentType: String, documentName: String): Result<String> {
        val path = "$userId/$documentType/$documentName"
        return documentRepository.createSignedUrl(bucket, path)
    }

    // List all documents of a specific type for a user
    suspend fun listDocuments(bucket: String, userId: String, documentType: String): Result<List<String>> {
        val path = "$userId/$documentType"
        return documentRepository.listDocuments(bucket, path)
    }

    // Get a document as a File
    suspend fun getDocumentAsFile(bucket: String, userId: String, documentType: String, documentName: String): Result<File> {
        val path = "$userId/$documentType/$documentName"
        return documentRepository.getDocumentAsFile(bucket, path)
    }
}
