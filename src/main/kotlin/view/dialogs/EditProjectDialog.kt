package ca.uwaterloo.view.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ca.uwaterloo.controller.ProfileController
import ca.uwaterloo.model.PersonalProject
import kotlinx.coroutines.runBlocking

@Composable
fun EditProjectDialog(
    project: PersonalProject? = null,
    onDismiss: () -> Unit,
    userId: String,
    profileController: ProfileController,
    onProjectEdited: () -> Unit,
    onProjectDeleted: () -> Unit
) {
    var projectName by remember { mutableStateOf(project?.projectName ?: "") }
    var description by remember { mutableStateOf(project?.description ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row for Edit Project text and Delete icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Edit Project", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    if (project != null) {
                        IconButton(
                            onClick = {
                                runBlocking {
                                    val result = profileController.deleteProject((project.id ?: 0).toString())
                                    result.onSuccess {
                                        onProjectDeleted()
                                        onDismiss()
                                    }.onFailure { error ->
                                        errorMessage = error.message ?: "Failed to delete project"
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource("TrashIcon.svg"),
                                contentDescription = "Delete"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Project Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
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

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            runBlocking {
                                val result = profileController.updateProject(userId,
                                    (project?.id ?: 0).toString(), projectName, description)
                                result.onSuccess {
                                    onProjectEdited()
                                    onDismiss()
                                }.onFailure {
                                    errorMessage = it.message ?: "Failed to update project"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF487896), contentColor = Color.White)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
