package ca.uwaterloo.view.dialogs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.controller.ProgressController
import ca.uwaterloo.persistence.DocumentRepository
import ca.uwaterloo.persistence.IJobApplicationRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import service.email
import java.awt.Desktop
import java.net.URI
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text

@Composable
fun EmailDialog(
    emails: List<email>,
    emailIndex: Int,
    userId: String,
    progressController: ProgressController,
    documentRepository: DocumentRepository,
    onNextEmail: () -> Unit,
    onClose: () -> Unit
) {
    var selectedJobId by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<Int?>(null) }
    var appliedJobs by remember { mutableStateOf<List<Pair<IJobApplicationRepository.JobProgress, String>>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var attachLinks by remember { mutableStateOf<List<String>>(emptyList()) }
    val statuses = listOf("APPLIED", "INTERVIEWING", "OFFER", "OTHER", "REJECTED")
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(emailIndex, userId) {
        appliedJobs = progressController.getCorrespondJobs(userId, emails[emailIndex].senderEmail)
    }


    Dialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(2f)
                .fillMaxHeight(0.95f)
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colors.surface
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Scrollable Content
                    Column(
                        modifier = Modifier
                            .weight(1f) // Let this column take available space for scrolling
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Dialog Title
                        Text(
                            text = "Job Application Update: Email ${emailIndex + 1} of ${emails.size}",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Email Subject
                        Text(
                            text = emails[emailIndex].subject,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Email Content
                        TextField(
                            value = emails[emailIndex].text,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp),
                            readOnly = true,
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Attachments Button
                        attachLinks = emails[emailIndex].attachLink
                        if (attachLinks.isNotEmpty()) {
                            Button(
                                onClick = { showDialog = true },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF487B96)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "View Attached Files",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (showDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDialog = false },
                                    title = {
                                        Text(
                                            text = "Attached Files",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    },
                                    text = {
                                        Column {
                                            if (attachLinks.isNotEmpty()) {
                                                attachLinks.forEach { link ->
                                                    ClickableText(
                                                        text = AnnotatedString(
                                                            text = link,
                                                            spanStyle = SpanStyle(textDecoration = TextDecoration.Underline) // Underline for hyperlink
                                                        ),
                                                        onClick = {
                                                            // Open the link in the default browser
                                                            if (Desktop.isDesktopSupported()) {
                                                                try {
                                                                    Desktop.getDesktop().browse(URI(link))
                                                                } catch (e: Exception) {
                                                                    e.printStackTrace() // Handle invalid URLs or errors
                                                                }
                                                            }
                                                        },
                                                        modifier = Modifier.padding(vertical = 4.dp)
                                                    )
                                                }
                                            } else {
                                                Text(
                                                    text = "No attached files available.",
                                                    modifier = Modifier.padding(vertical = 8.dp),
                                                    style = MaterialTheme.typography.body2
                                                )
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                showDialog = false
                                            }
                                        ) {
                                            Text(text = "Close")
                                        }
                                    }
                                )
                            }
                        }



                        Spacer(modifier = Modifier.height(16.dp))

                        // Status Selection
                        LaunchedEffect(emails[emailIndex].text) {
                            coroutineScope.launch {
                                val classifiedStatus = try {
                                    progressController.classifyApplicationStatus(emails[emailIndex].text)
                                } catch (e: Exception) {
                                    "OTHER"
                                }
                                selectedStatus = statuses.indexOf(classifiedStatus).takeIf { it >= 0 } ?: 0
                            }
                        }

                        Text(
                            "Snowmail has analyzed this and automatically assigned the most likely status to your job application. If this is incorrect, you can manually select the appropriate status from the options below:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val statuses = listOf("APPLIED", "INTERVIEWING", "OFFER", "OTHER", "REJECTED")
                            statuses.forEachIndexed { index, status ->
                                Button(
                                    onClick = { selectedStatus = index },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = if (selectedStatus == index) MaterialTheme.colors.primary else Color.LightGray
                                    )
                                ) {
                                    Text(status, color = if (selectedStatus == index) Color.White else Color.Black)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Job Selection
                        Text(
                            text = "Please select the title for this job:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                        ) {

                            items(appliedJobs) { (job, jobId) ->
                                Button(
                                    onClick = { selectedJobId = jobId },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = if (selectedJobId == jobId) MaterialTheme.colors.primary else Color.LightGray
                                    )
                                ) {
                                    Text(
                                        text = "${job.jobTitle} - ${job.companyName}",
                                        color = if (selectedJobId == jobId) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                           if (selectedJobId != null && selectedStatus != null) {
                                coroutineScope.launch {
                                    progressController.modifyStatus(selectedJobId!!, selectedStatus!! + 1)
                                    if (emailIndex + 1 < emails.size) {
                                        selectedJobId = null
                                        onNextEmail()
                                    } else {
                                        onClose()
                                    }
                                    runBlocking {
                                        println(emails)
                                        println(emails[emailIndex].fileNames)
                                        progressController.deleteAttachments(
                                            emails[emailIndex].fileNames,
                                            documentRepository
                                        )
                                    }

                                }
                            } else  {
                               errorMessage = "Please select a status and a job title before proceeding."
                           }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (selectedJobId != null && selectedStatus != null) MaterialTheme.colors.primary else Color.Gray
                        ),
                        enabled = selectedJobId != null && selectedStatus != null
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }
}
