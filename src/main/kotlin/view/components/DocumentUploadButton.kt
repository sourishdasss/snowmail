package ca.uwaterloo.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.unit.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import ca.uwaterloo.controller.DocumentController
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import ca.uwaterloo.view.pages.UserSession

@Composable
fun DocumentUploadButton(documentController: DocumentController) {
    val coroutineScope = rememberCoroutineScope()
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var showFileDialog by remember { mutableStateOf(false) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    var selectedDocumentType by remember { mutableStateOf<String?>(null) }
    val documentTypes = listOf("Resume", "Cover Letter", "Transcript", "Certificates", "Others")

    Column {
        Button(
            onClick = { showDropdownMenu = true },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .width(130.dp)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White
            )
        ) {
            Text(text = "Upload", color = Color.White, fontSize = 14.sp)
        }

        DropdownMenu(
            expanded = showDropdownMenu,
            onDismissRequest = { showDropdownMenu = false }
        ) {
            documentTypes.forEach { documentType ->
                DropdownMenuItem(onClick = {
                    selectedDocumentType = documentType
                    showDropdownMenu = false
                    showFileDialog = true
                }) {
                    Text(text = documentType)
                }
            }
        }

        if (showFileDialog) {
            val fileDialog = remember {
                FileDialog(Frame(), "Select PDF", FileDialog.LOAD).apply {
                    isVisible = true
                }
            }
            selectedFile = fileDialog.file?.let { File(fileDialog.directory, it) }
            println("selectedFile: $selectedFile")
            showFileDialog = false
        }

        LaunchedEffect(selectedFile) {
            selectedFile?.let { file ->
                selectedDocumentType?.let { documentType ->
                    coroutineScope.launch {
                        println("entered coroutine to upload document")
                        val result = documentController.uploadDocument(
                            bucket = "user_documents",
                            userId = UserSession.userId ?: "DefaultUserId",
                            documentType = documentType,
                            documentName = file.name,
                            file = file
                        )
                        result.onSuccess {
                            println("Upload successful: $it")
                        }.onFailure { error ->
                            println("Error uploading document: ${error.message}; check after error message to see if handling is successful")
                            // check if the file already exists, if it does, delete it and re-upload
                            if (error.message?.contains("already exists") == true) {
                                val deleteResult = documentController.deleteDocument(
                                    bucket = "user_documents",
                                    userId = UserSession.userId ?: "DefaultUserId",
                                    documentType = documentType,
                                    documentName = file.name
                                )
                                deleteResult.onSuccess {
                                    println("Existing document deleted: $it")
                                    // Re-upload the document
                                    val reuploadResult = documentController.uploadDocument(
                                        bucket = "user_documents",
                                        userId = UserSession.userId ?: "DefaultUserId",
                                        documentType = documentType,
                                        documentName = file.name,
                                        file = file
                                    )
                                    reuploadResult.onSuccess {
                                        println("Re-upload successful: $it")
                                    }.onFailure { reuploadError ->
                                        println("Error re-uploading document: ${reuploadError.message}")
                                    }
                                }.onFailure { deleteError ->
                                    println("Error deleting existing document: ${deleteError.message}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
