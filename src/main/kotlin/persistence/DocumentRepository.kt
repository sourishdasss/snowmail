package ca.uwaterloo.persistence

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import java.io.File
import java.io.InputStream
import kotlin.time.Duration.Companion.minutes

class DocumentRepository(private val supabase: SupabaseClient) : IDocumentRepository {

    private val storage = supabase.storage

    // Upload a document to the specified bucket
    override suspend fun uploadDocument(bucket: String, path: String, file: File): Result<String> {
        return try {
            if (!file.exists()) {
                println("File does not exist: ${file.path}")
                return Result.failure(Exception("File does not exist: ${file.path}"))
            }
            val fileContent = try {
                file.readBytes()
            } catch (e: Exception) {
                println("Error reading file content: ${e.message}")
                return Result.failure(Exception("Error reading file content: ${e.message}"))
            }
            try {
                storage.from(bucket).upload(path, fileContent)
            } catch (e: Exception) {
                println("Error uploading file to storage: ${e.message}")
                return Result.failure(Exception("Error uploading file to storage: ${e.message}"))
            }
            println("File uploaded successfully")
            Result.success("Document uploaded successfully.")
        } catch (e: Exception) {
            println("Error uploading document: ${e.message}")
            Result.failure(Exception("Error uploading document: ${e.message}"))
        }
    }

    // Download a document from the specified bucket as a ByteArray
    override suspend fun downloadDocument(bucket: String, path: String): Result<ByteArray> {
        return try {
            val fileContent = storage.from(bucket).downloadAuthenticated(path)
            Result.success(fileContent)
        } catch (e: Exception) {
            Result.failure(Exception("Error downloading document: ${e.message}"))
        }
    }

    // Delete a document from the specified bucket
    override suspend fun deleteDocument(bucket: String, path: String): Result<String> {
        return try {
            storage.from(bucket).delete(path)
            Result.success("Document deleted successfully.")
        } catch (e: Exception) {
            Result.failure(Exception("Error deleting document: ${e.message}"))
        }
    }

    // Create a signed URL to view a document
    override suspend fun createSignedUrl(bucket: String, path: String): Result<String> {
        return try {
            val url = storage.from(bucket).createSignedUrl(path, 5.minutes)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(Exception("Error creating signed URL: ${e.message}"))
        }
    }

    // List all documents of a specific type for a user
    override suspend fun listDocuments(bucket: String, path: String): Result<List<String>> {
        return try {
            val files = storage.from(bucket).list(path)
            val fileNames = files.map { it.name }
            Result.success(fileNames)
        } catch (e: Exception) {
            Result.failure(Exception("Error listing documents: ${e.message}"))
        }
    }

    // Get a document as a File
    override suspend fun getDocumentAsFile(bucket: String, path: String): Result<File> {
        return try {
            val fileContent = storage.from(bucket).downloadAuthenticated(path)
            val tempFile = File.createTempFile("document", ".tmp")
            tempFile.writeBytes(fileContent)
            Result.success(tempFile)
        } catch (e: Exception) {
            Result.failure(Exception("Error getting document as file: ${e.message}"))
        }
    }

    // Upload an email attachment to the specified bucket
    override suspend fun uploadEmailAttachment(fileName: String, inputStream: InputStream): Result<String> {
        val tempFile = File.createTempFile("attachment", ".tmp")
        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output, bufferSize = 8 * 1024) // 8 KB buffer
            }
        }
        val path = "email_attachments/$fileName"
        return try {
            storage.from("user_documents").upload(path, tempFile.readBytes())
            val url = storage.from("user_documents").createSignedUrl(path, 20.minutes)
            tempFile.delete()
            Result.success(url)
        } catch (e: Exception) {
            println(e.message)
            storage.from("user_documents").update(path, tempFile.readBytes())
            val url = storage.from("user_documents").createSignedUrl(path, 20.minutes)
            tempFile.delete()
            Result.success(url)
        }
    }

    // Delete email attachments from the specified bucket
    override suspend fun deleteAttachments(files: List<String>): Result<String> {
        return try {
            for (file in files) {
                val path = "email_attachments/$file"
                println(path)
                storage.from("user_documents").delete(path)
            }
            Result.success("Attachments deleted successfully.")
        } catch (e: Exception) {
            println(e.message)
            Result.failure(Exception("Error deleting attachments: ${e.message}"))
        }
    }
}
