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
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun DocViewButton(
    document: String,
    documentType: String,
    documentController: DocumentController,
    coroutineScope: CoroutineScope
) {
    IconButton(
        onClick = {
            coroutineScope.launch {
                val encodedDocument = URLEncoder.encode(document, StandardCharsets.UTF_8.toString())
                val result = documentController.viewDocument(
                    "user_documents",
                    UserSession.userId ?: "DefaultUserId",
                    documentType,
                    document
                )
                result.onSuccess { url ->
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(URI(url))
                    } else {
                        println("Desktop is not supported. Please open the URL manually: $url")
                    }
                }.onFailure { error ->
                    println("Error creating signed URL: ${error.message}")
                }
            }
        },
        modifier = Modifier.size(20.dp)
    ) {
        Icon(
            painter = painterResource("ViewIcon.svg"),
            contentDescription = "View",
            tint = MaterialTheme.colors.primary
        )
    }
}
