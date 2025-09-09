package ca.uwaterloo.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
//import kotlinx.serialization.Serializable

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import ca.uwaterloo.controller.DocumentController

import ca.uwaterloo.view.pages.UserSession
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource

// A function that is a row in the document page.
// The row has a type that is passed to it as a parameter like "Resume" or "Cover Letter".
// When clicked, it will dropdown and display a list of documents that are the same type as the row type.

@Composable
fun DocDropdownRow(documentType: String, documentController: DocumentController) {
    var expanded by remember { mutableStateOf(false) }
    var documentList by remember { mutableStateOf<List<String>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    var i by remember { mutableStateOf(0) }

    LaunchedEffect(expanded, i) {
        if (expanded|| documentList.isEmpty()) {
            coroutineScope.launch {
                val result = documentController.listDocuments("user_documents", UserSession.userId ?: "DefaultUserId", documentType)
                result.onSuccess { documents ->
                    documentList = documents
                    i++
                }.onFailure { error ->
                    println("Error listing documents: ${error.message}")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colors.onSecondary.copy(alpha = 0.2f))
                .fillMaxWidth()
                .height(55.dp)
                .padding(horizontal = 20.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { expanded = !expanded }
        ) {
            Text(
                text = documentType,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (expanded) {
                Image(
                    painter = painterResource("arrow_up.png"),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
                )
            } else {
                Image(
                    painter = painterResource("arrow_down.png"),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
                )
            }
        }
        if (expanded) {
            documentList.forEach { document ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = document,
                            modifier = Modifier.weight(0.6f),
                            color = MaterialTheme.colors.secondary,
                        )
                        Spacer(modifier = Modifier.width(32.dp))
                        DocViewButton(
                            document = document,
                            documentType = documentType,
                            documentController = documentController,
                            coroutineScope = coroutineScope
                        )
                        Spacer(modifier = Modifier.width(24.dp))
                        DocDeleteButton(
                            document = document,
                            documentType = documentType,
                            documentController = documentController,
                            coroutineScope = coroutineScope
                        ) {
                            documentList = documentList.filter { it != document }
                        }
                    }
                }
            }
        }
    }
}
