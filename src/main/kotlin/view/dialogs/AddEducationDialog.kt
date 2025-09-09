package ca.uwaterloo.view.dialogs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun AddEducationDialog(
    onDismiss: () -> Unit,
    userId: String,
    profileController: ProfileController,
    onEducationAdded: () -> Unit
) {
    var schoolName by remember { mutableStateOf("") }
    var major by remember { mutableStateOf("") }
    var degreeType by remember { mutableStateOf("") }
    var gpa by remember { mutableStateOf("") }
    var startMonth by remember { mutableStateOf("") }
    var startYear by remember { mutableStateOf("") }
    var endMonth by remember { mutableStateOf("") }
    var endYear by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Validation error states
    var schoolNameError by remember { mutableStateOf(false) }
    var majorError by remember { mutableStateOf(false) }
    var degreeTypeError by remember { mutableStateOf(false) }
    var startMonthError by remember { mutableStateOf(false) }
    var startYearError by remember { mutableStateOf(false) }
    var endMonthError by remember { mutableStateOf(false) }
    var endYearError by remember { mutableStateOf(false) }
    var gpaError by remember { mutableStateOf(false) }

    val degreeTypes = listOf("High School Diploma/GED", "Associate's Degree/College Diploma", "Bachelor's Degree", "Master's Degree", "Doctorate Degree", "Other")
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Add Education", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = schoolName,
                    onValueChange = {
                        schoolName = it
                        schoolNameError = it.isEmpty()
                    },
                    label = { Text("School Name") },
                    isError = schoolNameError,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = major,
                    onValueChange = {
                        major = it
                        majorError = it.isEmpty()
                    },
                    label = { Text("Major") },
                    isError = majorError,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.TopStart)
                ) {
                    OutlinedTextField(
                        value = degreeType,
                        onValueChange = {},
                        label = { Text("Degree Type") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Select Degree Type",
                                modifier = Modifier.clickable { expanded = true }
                            )
                        },
                        modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                        isError = degreeTypeError,
                        readOnly = true
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        degreeTypes.forEach { type ->
                            DropdownMenuItem(onClick = {
                                degreeType = type
                                degreeTypeError = false
                                expanded = false
                            }) {
                                Text(type)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = gpa,
                    onValueChange = {
                        if (it.toFloatOrNull() != null || it.isEmpty()) {
                            gpa = it
                            gpaError = false
                        } else {
                            gpaError = true
                        }
                    },
                    label = { Text("GPA") },
                    isError = gpaError,
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
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endYear,
                        onValueChange = {
                            endYear = it
                            endYearError = it.length != 4 || it.toIntOrNull() == null
                        },
                        label = { Text("End Year") },
                        isError = endYearError,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (
                                schoolNameError || majorError || degreeType.isEmpty() ||
                                startMonthError || startYearError || endMonthError || endYearError
                            ) {
                                errorMessage = "Please enter all fields correctly."
                            } else {
                                runBlocking {
                                    try {
                                        val startDate = LocalDate(startYear.toInt(), startMonth.toInt(), 1)
                                        val endDate = LocalDate(endYear.toInt(), endMonth.toInt(), 1)
                                        val gpaValue = gpa.toFloatOrNull()
                                        val degreeId = degreeTypes.indexOf(degreeType) + 1

                                        val result = profileController.addEducation(
                                            userId = userId,
                                            degreeId = degreeId,
                                            major = major,
                                            gpa = gpaValue,
                                            startDate = startDate,
                                            endDate = endDate,
                                            institutionName = schoolName
                                        )

                                        result.onSuccess {
                                            onEducationAdded()
                                            onDismiss()
                                        }.onFailure { error ->
                                            errorMessage = error.message ?: "Failed to add education record."
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "An unexpected error occurred. Please check your inputs."
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
