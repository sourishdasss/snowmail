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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ca.uwaterloo.controller.ProfileController
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun AddWorkExperienceDialog(
    onDismiss: () -> Unit,
    userId: String,
    profileController: ProfileController,
    onWorkExperienceAdded: () -> Unit
) {
    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var positionTitle by remember { mutableStateOf("") }
    var startMonth by remember { mutableStateOf("") }
    var startYear by remember { mutableStateOf("") }
    var endMonth by remember { mutableStateOf("") }
    var endYear by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCurrentlyWorking by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Error state tracking
    var companyError by remember { mutableStateOf(false) }
    var positionTitleError by remember { mutableStateOf(false) }
    var startMonthError by remember { mutableStateOf(false) }
    var startYearError by remember { mutableStateOf(false) }
    var endMonthError by remember { mutableStateOf(false) }
    var endYearError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Add Work Experience", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = company,
                    onValueChange = {
                        company = it
                        companyError = it.isEmpty()
                    },
                    label = { Text("Company") },
                    isError = companyError,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = positionTitle,
                    onValueChange = {
                        positionTitle = it
                        positionTitleError = it.isEmpty()
                    },
                    label = { Text("Position Title") },
                    isError = positionTitleError,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = startMonth,
                        onValueChange = {
                            startMonth = it
                            startMonthError = it.toIntOrNull() !in 1..12 || it.startsWith("0") || it.isEmpty()
                        },
                        label = { Text("Start Month") },
                        isError = startMonthError,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("(ex: 7 for July)") }
                    )
                    OutlinedTextField(
                        value = startYear,
                        onValueChange = {
                            startYear = it
                            startYearError = it.length != 4 || it.toIntOrNull() == null
                        },
                        label = { Text("Start Year") },
                        isError = startYearError,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = endMonth,
                        onValueChange = {
                            endMonth = it
                            endMonthError = it.toIntOrNull() !in 1..12 || it.startsWith("0") || it.isEmpty()
                        },
                        label = { Text("End Month") },
                        isError = endMonthError,
                        modifier = Modifier.weight(1f),
                        enabled = !isCurrentlyWorking
                    )
                    OutlinedTextField(
                        value = endYear,
                        onValueChange = {
                            endYear = it
                            endYearError = it.length != 4 || it.toIntOrNull() == null
                        },
                        label = { Text("End Year") },
                        isError = endYearError,
                        modifier = Modifier.weight(1f),
                        enabled = !isCurrentlyWorking
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Checkbox(checked = isCurrentlyWorking, onCheckedChange = { isCurrentlyWorking = it })
                    Text("I currently work here")
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(10.dp))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (companyError || positionTitleError || startMonthError || startYearError || (!isCurrentlyWorking && (endMonthError || endYearError))) {
                                errorMessage = "Please fix errors in the form."
                            } else {
                                runBlocking {
                                    try {
                                        val startDate = LocalDate(startYear.toInt(), startMonth.toInt(), 1)
                                        val endDate = if (isCurrentlyWorking) Clock.System.now().toLocalDateTime(
                                            TimeZone.currentSystemDefault()).date else LocalDate(endYear.toInt(), endMonth.toInt(), 1)
                                        val result = profileController.addWorkExperience(
                                            userId = userId,
                                            companyName = company,
                                            currentlyWorking = isCurrentlyWorking,
                                            title = positionTitle,
                                            startDate = startDate,
                                            endDate = endDate,
                                            description = description.takeIf { it.isNotEmpty() }
                                        )

                                        result.onSuccess {
                                            onWorkExperienceAdded()
                                            onDismiss()
                                        }.onFailure { error ->
                                            errorMessage = error.message ?: "Failed to add work experience."
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Invalid date format or missing input"
                                    }
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
