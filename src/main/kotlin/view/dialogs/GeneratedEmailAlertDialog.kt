package ca.uwaterloo.view.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ca.uwaterloo.controller.DocumentController
import controller.SendEmailController
import integration.SupabaseClient
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource

@Composable
fun GeneratedEmailAlertDialog(
    userId: String,
    title: String,
    initialText: String,
    reciepientAddress: String,
    jobTitle: String,
    companyName: String,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    var recipientEmailAddy by remember { mutableStateOf(reciepientAddress) }
    var emailSubject by remember { mutableStateOf("Application for $jobTitle at $companyName") }
    var showAttachDialog by remember { mutableStateOf(false) }
    val attachedDocuments = remember { mutableStateListOf<Pair<String, String>>() }
    var errorMessage by remember { mutableStateOf("") }
    var sendEmailTrigger by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    if (showAttachDialog) {
        AttachDocumentDialog(
            userId = userId,
            onDismissRequest = { showAttachDialog = false },
            onConfirm = { selectedDocuments ->
                attachedDocuments.clear()
                attachedDocuments.addAll(selectedDocuments)
                showAttachDialog = false
            },
            documentController = DocumentController(SupabaseClient().documentRepository)
        )
    }

    if (sendEmailTrigger) {
        LaunchedEffect(sendEmailTrigger) {
            try {
                val emailsendingController = SendEmailController(
                    SupabaseClient().jobApplicationRepository,
                    SupabaseClient().documentRepository
                )
                val returnMessage = emailsendingController.send_email(
                    recipient = recipientEmailAddy,
                    subject = emailSubject,
                    text = text,
                    buckets = List(attachedDocuments.size) { "user_documents" },
                    documentsType = attachedDocuments.map { it.second },
                    documentsName = attachedDocuments.map { it.first },
                    userID = userId,
                    jobTitle = jobTitle,
                    companyName = companyName
                )
                if (returnMessage == "Success") {
                    errorMessage = ""
                    onConfirm(text)
                    showSuccessDialog = true
                } else {
                    errorMessage = returnMessage
                }
            } catch (e: Exception) {
                errorMessage = "Error sending email: ${e.message}"
                println("Error sending email: ${e.message}")
            } finally {
                sendEmailTrigger = false
            }
        }
    }

    if (showSuccessDialog) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colors.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource("sendCheck.svg"),
                            modifier = Modifier.size(32.dp),
                            contentDescription = "Email Sent Successfully",
                            tint = MaterialTheme.colors.primary
                        )

                        Text(
                            text = "Email Sent Successfully!",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.secondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                onDismissRequest()
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
        return
    }


    Dialog(onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colors.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )
                        }

                        OutlinedTextField(
                            value = recipientEmailAddy,
                            onValueChange = { recipientEmailAddy = it },
                            label = { Text("Recipient Email") },
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = emailSubject,
                            onValueChange = { emailSubject = it },
                            label = { Text("Subject") },
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { showAttachDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Attach Document")
                        }
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                        ) {
                            var text = "Attached Files:"
                            attachedDocuments.forEach { (name, type) ->
                                text += " $name"
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = text,
                                color = MaterialTheme.colors.secondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Editable email content") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .background(Color.Transparent),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.Transparent,
                                //unfocusedLabelColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = onDismissRequest,
                            ) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    when {
                                        recipientEmailAddy.isBlank() -> {
                                            errorMessage = "Recipient email cannot be empty."
                                        }

                                        else -> {
                                            errorMessage = ""
                                            sendEmailTrigger = true
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.primary,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Send")
                            }
                        }
                    }
            }
        }
    }
}
