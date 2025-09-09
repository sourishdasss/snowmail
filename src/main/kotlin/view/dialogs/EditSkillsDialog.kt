package ca.uwaterloo.view.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.controller.ProfileController
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import ca.uwaterloo.view.components.SkillChip

@Composable
fun EditSkillsDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    userId: String,
    profileController: ProfileController,
    initialSkills: List<String>
) {
    var skillInput by remember { mutableStateOf("") }
    var isDuplicateSkill by remember { mutableStateOf(false) }
    var isInvalidSkill by remember { mutableStateOf(false) }
    var isTooLongSkill by remember { mutableStateOf(false) }
    var selectedSkills = remember { mutableStateListOf<String>().apply { addAll(initialSkills) } }
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
                Text("Edit Skills", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                Text(
                    text = "Click a skill to delete it from your list.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = skillInput,
                    onValueChange = {
                        skillInput = it.take(15)
                        isDuplicateSkill = false
                        isInvalidSkill = false
                        isTooLongSkill = skillInput.length > 15
                    },
                    label = { Text("Add a new skill") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isDuplicateSkill || isInvalidSkill || isTooLongSkill
                )

                if (isDuplicateSkill) {
                    Text(
                        text = "This skill has already been added.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (isInvalidSkill) {
                    Text(
                        text = "Invalid skill. Please enter valid text.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (isTooLongSkill) {
                    Text(
                        text = "Skill cannot exceed 15 characters.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Button(
                    onClick = {
                        if (skillInput.isBlank()) {
                            isInvalidSkill = true
                        } else if (skillInput in selectedSkills) {
                            isDuplicateSkill = true
                        } else {
                            selectedSkills.add(skillInput.trim())
                            skillInput = ""
                            isDuplicateSkill = false
                            isInvalidSkill = false
                            isTooLongSkill = false
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = skillInput.isNotBlank() && !isTooLongSkill,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Add")
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 120.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedSkills) { skill ->
                        SkillChip(skill = skill) {
                            selectedSkills.remove(skill)
                        }
                    }
                }


                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colors.primary
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    selectedSkills.forEach { skill ->
                                        if (skill !in initialSkills) {
                                            profileController.addSkill(userId, skill)
                                        }
                                    }

                                    initialSkills.forEach { skill ->
                                        if (skill !in selectedSkills) {
                                            profileController.deleteSkill(userId, skill)
                                        }
                                    }

                                    onSave()
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "Failed to save skills."
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
