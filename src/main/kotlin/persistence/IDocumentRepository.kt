package ca.uwaterloo.persistence

import java.io.File
import java.io.InputStream

interface IDocumentRepository {
    suspend fun uploadDocument(bucket: String, path: String, file: File): Result<String>
    suspend fun downloadDocument(bucket: String, path: String): Result<ByteArray>
    suspend fun deleteDocument(bucket: String, path: String): Result<String>
    suspend fun createSignedUrl(bucket: String, path: String): Result<String>
    suspend fun listDocuments(bucket: String, path: String): Result<List<String>>
    suspend fun uploadEmailAttachment(fileName: String, inputStream: InputStream): Result<String>
    suspend fun deleteAttachments(files: List<String>): Result<String>
    suspend fun getDocumentAsFile(bucket: String, path: String): Result<File>
}
