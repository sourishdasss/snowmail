package ca.uwaterloo.view.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ca.uwaterloo.controller.DocumentController
import ca.uwaterloo.view.pages.UserSession
//import ca.uwaterloo.view.components.FetchUserProfileData
//import io.ktor.client.engine.cio.*
import kotlinx.coroutines.launch


@Composable
fun AttachDocumentDialog(
    userId: String,
    onDismissRequest: () -> Unit,
    onConfirm: (List<Pair<String, String>>) -> Unit,
    documentController: DocumentController
) {
    val documentTypes = listOf("Resume", "Cover Letter", "Transcript", "Certificates", "Others")
    val selectedDocuments = remember { mutableStateListOf<Pair<String, String>>() }
    val documents = remember { mutableStateListOf<Pair<String, String>>() }

    val coroutineScope = rememberCoroutineScope()

    val allDocs = remember { mutableStateListOf<Pair<String, String>>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {

            documentTypes.forEach { documentType ->
                val result = documentController.listDocuments("user_documents", UserSession.userId ?: "DefaultUserId", documentType)
                result.onSuccess { docs ->
                    allDocs.addAll(docs.map { doc -> Pair(doc, documentType) })
                }.onFailure { error ->
                    println("Error listing documents: ${error.message}")
                }
            }
            documents.addAll(allDocs)
            isLoading = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Attach Documents", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black) },
        text = {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column {
                    documents.forEach { document ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = selectedDocuments.contains(document),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        selectedDocuments.add(document)
                                    } else {
                                        selectedDocuments.remove(document)
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colors.primary,
                                    uncheckedColor = MaterialTheme.colors.primary,
                                    checkmarkColor = Color.White
                                )
                            )
                            Column {
                                Text(document.first, fontWeight = FontWeight.Bold)
                                Text("Type: " + document.second)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedDocuments)
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White
                )
            ) {
                Text("Attach")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancel")
            }
        }
    )
}
