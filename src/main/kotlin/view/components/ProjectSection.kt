package ca.uwaterloo.view.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import ca.uwaterloo.controller.ProfileController
import ca.uwaterloo.model.PersonalProject
import ca.uwaterloo.view.pages.SectionTitle
import ca.uwaterloo.view.dialogs.AddProjectDialog
import ca.uwaterloo.view.dialogs.EditProjectDialog

@Composable
fun ProjectSection(
    userId: String,
    profileController: ProfileController,
    projectList: List<PersonalProject>,
    showProjectDialog: Boolean,
    showEditProjectDialog: Boolean,
    selectedProject: PersonalProject?,
    onProjectAdded: () -> Unit,
    onProjectEdited: () -> Unit,
    onProjectDeleted: () -> Unit,
    onShowProjectDialogChange: (Boolean) -> Unit,
    onShowEditProjectDialogChange: (Boolean) -> Unit,
    onSelectedProjectChange: (PersonalProject?) -> Unit
) {
    Column {
        SectionTitle("Projects")
        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            if (projectList.isEmpty()) {
                Text(
                    text = "No projects added",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                Column(modifier = Modifier.padding(8.dp)) {
                    projectList.forEach { project ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectedProjectChange(project)
                                    onShowEditProjectDialogChange(true)
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    text = project.projectName,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = project.description ?: "",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colors.secondary
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        onSelectedProjectChange(null)
                        onShowProjectDialogChange(true)
                    },
                    modifier = Modifier.size(15.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = MaterialTheme.colors.primary
                    )
                }
            }

            if (showProjectDialog) {
                AddProjectDialog(
                    onDismiss = { onShowProjectDialogChange(false) },
                    userId = userId,
                    profileController = profileController,
                    onProjectAdded = onProjectAdded
                )
            }

            if (showEditProjectDialog) {
                EditProjectDialog(
                    onDismiss = { onShowEditProjectDialogChange(false) },
                    userId = userId,
                    profileController = profileController,
                    project = selectedProject,
                    onProjectEdited = {
                        onProjectEdited()
                        onSelectedProjectChange(null)
                        onShowEditProjectDialogChange(false)
                    },
                    onProjectDeleted = {
                        onProjectDeleted()
                        onSelectedProjectChange(null)
                        onShowEditProjectDialogChange(false)
                    }
                )
            }
        }
    }
}
