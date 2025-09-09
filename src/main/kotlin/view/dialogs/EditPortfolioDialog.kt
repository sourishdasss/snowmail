package ca.uwaterloo.view.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ca.uwaterloo.controller.ProfileController
import kotlinx.coroutines.launch

@Composable
fun EditPortfolioDialog(
    userId: String,
    profileController: ProfileController,
    linkedInUrl: String?,
    githubUrl: String?,
    portfolioUrl: String?,
    onDismiss: () -> Unit,
    onLinksUpdated: () -> Unit
) {
    var linkedInInput by remember { mutableStateOf(linkedInUrl ?: "") }
    var githubInput by remember { mutableStateOf(githubUrl ?: "") }
    var portfolioInput by remember { mutableStateOf(portfolioUrl ?: "") }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Edit Portfolio",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                OutlinedTextField(
                    value = linkedInInput,
                    onValueChange = { linkedInInput = it },
                    label = { Text("LinkedIn URL") },
                    placeholder = { Text("https://www.linkedin.com/in/...") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = githubInput,
                    onValueChange = { githubInput = it },
                    label = { Text("GitHub URL") },
                    placeholder = { Text("https://github.com/...") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = portfolioInput,
                    onValueChange = { portfolioInput = it },
                    label = { Text("Portfolio URL") },
                    placeholder = { Text("https://...") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                val result = profileController.updateUserLinks(
                                    userId = userId,
                                    linkedinUrl = linkedInInput.ifBlank { null },
                                    githubUrl = githubInput.ifBlank { null },
                                    personalWebsiteUrl = portfolioInput.ifBlank { null }
                                )
                                result.onSuccess {
                                    onLinksUpdated()
                                    onDismiss()
                                }.onFailure { error ->
                                    errorMessage = error.message ?: "Failed to update portfolio links."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
