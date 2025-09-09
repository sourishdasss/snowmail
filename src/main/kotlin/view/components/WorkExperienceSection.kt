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
import ca.uwaterloo.model.WorkExperience
import ca.uwaterloo.view.pages.SectionTitle
import ca.uwaterloo.view.dialogs.AddWorkExperienceDialog
import ca.uwaterloo.view.dialogs.EditWorkExperienceDialog

@Composable
fun WorkExperienceSection(
    userId: String,
    profileController: ProfileController,
    workExperienceList: List<WorkExperience>,
    showWorkExperienceDialog: Boolean,
    showEditWorkExperienceDialog: Boolean,
    selectedWorkExperience: WorkExperience?,
    onWorkExperienceAdded: () -> Unit,
    onWorkExperienceEdited: () -> Unit,
    onWorkExperienceDeleted: () -> Unit,
    onShowWorkExperienceDialogChange: (Boolean) -> Unit,
    onShowEditWorkExperienceDialogChange: (Boolean) -> Unit,
    onSelectedWorkExperienceChange: (WorkExperience?) -> Unit
) {
    Column {
        SectionTitle("Work Experience")
        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            if (workExperienceList.isEmpty()) {
                Text(
                    text = "No experiences added",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                Column(modifier = Modifier.padding(8.dp)) {
                    workExperienceList.forEach { experience ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectedWorkExperienceChange(experience)
                                    onShowEditWorkExperienceDialogChange(true)
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "${experience.companyName} - ${experience.title}",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "${experience.startDate} to ${experience.endDate ?: "Present"}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colors.secondary
                                )
                                if (!experience.description.isNullOrEmpty()) {
                                    Text(
                                        text = experience.description,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colors.secondary
                                    )
                                }
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
                        onSelectedWorkExperienceChange(null)
                        onShowWorkExperienceDialogChange(true)
                    },
                    modifier = Modifier.size(15.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color(0xFF487896)
                    )
                }
            }

            if (showWorkExperienceDialog) {
                AddWorkExperienceDialog(
                    onDismiss = { onShowWorkExperienceDialogChange(false) },
                    userId = userId,
                    profileController = profileController,
                    onWorkExperienceAdded = onWorkExperienceAdded
                )
            }

            if (showEditWorkExperienceDialog) {
                EditWorkExperienceDialog(
                    onDismiss = { onShowEditWorkExperienceDialogChange(false) },
                    userId = userId,
                    profileController = profileController,
                    experience = selectedWorkExperience,
                    onWorkExperienceEdited = {
                        onWorkExperienceEdited()
                        onSelectedWorkExperienceChange(null)
                        onShowEditWorkExperienceDialogChange(false)
                    },
                    onWorkExperienceDeleted = {
                        onWorkExperienceDeleted()
                        onSelectedWorkExperienceChange(null)
                        onShowEditWorkExperienceDialogChange(false)
                    }
                )
            }
        }
    }
}
