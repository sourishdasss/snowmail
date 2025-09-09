package ca.uwaterloo.view.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ca.uwaterloo.controller.DocumentController
import ca.uwaterloo.view.pages.UserSession

@Composable
fun DocDeleteButton(
    document: String,
    documentType: String,
    documentController: DocumentController,
    coroutineScope: CoroutineScope,
    onDeleteSuccess: () -> Unit
) {
    IconButton(
        onClick = {
            coroutineScope.launch {
                val result = documentController.deleteDocument(
                    "user_documents",
                    UserSession.userId ?: "DefaultUserId",
                    documentType,
                    document
                )
                result.onSuccess {
                    onDeleteSuccess()
                    println("Document deleted successfully")
                }.onFailure { error ->
                    println("Error deleting document: ${error.message}")
                }
            }
        },
        modifier = Modifier.size(20.dp)
    ) {
        Icon(
            painter = painterResource("TrashIcon.svg"),
            contentDescription = "Delete",
            tint = MaterialTheme.colors.primary
        )
    }
}
