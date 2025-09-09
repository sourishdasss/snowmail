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
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.view.pages.SectionTitle
import ca.uwaterloo.view.dialogs.AddEducationDialog
import ca.uwaterloo.view.dialogs.EditEducationDialog

@Composable
fun EducationSection(
    userId: String,
    profileController: ProfileController,
    educationList: List<EducationWithDegreeName>,
    showEducationDialog: Boolean,
    showEditEducationDialog: Boolean,
    selectedEducation: EducationWithDegreeName?,
    onEducationAdded: () -> Unit,
    onEducationEdited: () -> Unit,
    onEducationDeleted: () -> Unit,
    onShowEducationDialogChange: (Boolean) -> Unit,
    onShowEditEducationDialogChange: (Boolean) -> Unit,
    onSelectedEducationChange: (EducationWithDegreeName?) -> Unit
) {
    Column {
        SectionTitle("Education")
        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            if (educationList.isEmpty()) {
                Text(
                    text = "No education records added",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                Column(modifier = Modifier.padding(8.dp)) {
                    educationList.forEach { education ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectedEducationChange(education)
                                    onShowEditEducationDialogChange(true)
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "${education.institutionName} - ${education.degreeName} in ${education.major}",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "${education.startDate} to ${education.endDate}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colors.secondary
                                )
                                Text(
                                    text = "GPA: ${education.gpa ?: "N/A"}",
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
                        onSelectedEducationChange(null)
                        onShowEducationDialogChange(true)
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

            if (showEducationDialog) {
                AddEducationDialog(
                    onDismiss = { onShowEducationDialogChange(false) },
                    userId = userId,
                    profileController = profileController,
                    onEducationAdded = onEducationAdded
                )
            }

            if (showEditEducationDialog) {
                EditEducationDialog(
                    onDismiss = { onShowEditEducationDialogChange(false) },
                    userId = userId,
                    profileController = profileController,
                    education = selectedEducation,
                    onEducationEdited = {
                        onEducationEdited()
                        onSelectedEducationChange(null)
                        onShowEditEducationDialogChange(false)
                    },
                    onEducationDeleted = {
                        onEducationDeleted()
                        onSelectedEducationChange(null)
                        onShowEditEducationDialogChange(false)
                    }
                )
            }
        }
    }
}
