package ca.uwaterloo.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.controller.ProfileController
import ca.uwaterloo.view.pages.SectionTitle
import ca.uwaterloo.view.dialogs.EditSkillsDialog
import kotlinx.coroutines.runBlocking
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun SkillsSection(
    userId: String,
    profileController: ProfileController,
    skills: List<String>,
    showSkillsDialog: Boolean,
    onSkillsAdded: () -> Unit,
    onSkillsDeleted: () -> Unit,
    onShowSkillsDialogChange: (Boolean) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Skills",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = { onShowSkillsDialogChange(true) },
                modifier = Modifier.size(15.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Skill",
                    tint = MaterialTheme.colors.primary
                )
            }
        }

        if (skills.isEmpty()) {
            Text(
                text = "No items added",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 200.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .padding(vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(skills) { skill ->
                    SkillChip(skill = skill, onDelete = {
                        runBlocking {
                            profileController.deleteSkill(userId, skill)
                            onSkillsDeleted()
                        }
                    })
                }
            }
        }

        if (showSkillsDialog) {
            EditSkillsDialog(
                onDismiss = { onShowSkillsDialogChange(false) },
                onSave = {
                    onSkillsAdded()
                    onShowSkillsDialogChange(false)
                },
                userId = userId,
                profileController = profileController,
                initialSkills = skills
            )
        }
    }
}
