package ca.uwaterloo.view.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ca.uwaterloo.controller.ProfileController
import ca.uwaterloo.service.EmailValidatingService
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.net.URI

@Composable
fun GmailLinkingDialog(
    onDismissRequest: () -> Unit,
    userId: String,
    profileController: ProfileController
) {
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        isLoading = true
        val accountResult = profileController.getUserLinkedGmailAccount(userId)
        val passwordResult = profileController.getUserGmailAppPassword(userId)

        accountResult.onSuccess { result ->
            account = result
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve linked Gmail account."
        }

        passwordResult.onSuccess { result ->
            password = result
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve App Password."
        }

        isLoading = false
    }

    if (successMessage) {
        Dialog(onDismissRequest = { successMessage = false }) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Gmail linked successfully!",
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            successMessage = false
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF487896),
                            contentColor = Color.White
                        )
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF487896))
                }
            } else {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                    ) {
                        Text(
                            text = "Gmail Linking",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val annotatedString = buildAnnotatedString {
                        append("To use Snowmail to send emails through your Gmail account, you need to grant Snowmail access to your Google account and Gmail services. \n")
                        append("Please follow the steps below to securely link your Gmail account to Snowmail:\n\n")
                        append("1. Open Google Account Settings --> Manage Your Google Account\n")
                        append("2. Go to the \"Security\" section.\n3. Scroll down to the \"How you sign in to Google\" section.\n")
                        append("4. Enable 2-Step Verification if it’s not already turned on.\n5. Return to the \"Security\" section.\n")
                        append("6. Search for ")
                        pushStringAnnotation(
                            tag = "URL",
                            annotation = "https://myaccount.google.com/apppasswords?continue=https://myaccount.google.com/security?utm_source%3Dchrome-settings&pli=1&rapt=AEjHL4O7uSuEpGMELA6bQTszK_VubA2-GRY3rBunsnzdDciaH3BN__4TE6hCe1MGty9OrzcIv9Xn6Znzj1vOj63EGq8fi46UtvBZw6BQ32N0WHermYS-x9Q"
                        )
                        withStyle(style = SpanStyle(color = MaterialTheme.colors.primary, textDecoration = TextDecoration.Underline)) {
                            append("App passwords")
                        }
                        append(" in the topmost search bar and hit Enter\n")

                        append("7. Follow these steps to create an app-specific password:\n")
                        append("   - Type \"Snowmail\" in the App name input box.\n")
                        append("   - Click Create to create an app password.\n")
                        append("   - Copy the app password provided by Google (e.g., abcd-efgh-ijkl-mnop).\n\n")
                        append("Enter your Gmail username and the app password below to securely connect your account.")
                    }

                    ClickableText(
                        text = annotatedString,
                        style = TextStyle(fontSize = 14.sp, color = MaterialTheme.colors.secondary),
                        onClick = { offset ->
                            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                .firstOrNull()?.let { annotation ->
                                    val uri = URI(annotation.item)
                                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                        Desktop.getDesktop().browse(uri)
                                    }
                                }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = account,
                            onValueChange = { account = it },
                            label = { Text("Gmail Account") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("App Password") },
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                if (account.isBlank() || !account.endsWith("@gmail.com")) {
                                    errorMessage = "Please enter a valid Gmail account ending with @gmail.com."
                                    return@Button
                                }

                                if (password.isBlank()) {
                                    errorMessage = "App Password cannot be empty."
                                    return@Button
                                }

                                val emailValidatingService = EmailValidatingService()
                                coroutineScope.launch {
                                    val isValid = profileController.verifyUserLinkedEmailAndPassword(account, password)
                                    if (isValid) {
                                        errorMessage = ""
                                        successMessage = true

                                        val accountResult = profileController.editUserLinkedGmailAccount(userId, account)
                                        val passwordResult = profileController.editUserGmailAppPassword(userId, password)
                                        if (accountResult.isSuccess && passwordResult.isSuccess) {
                                            successMessage = true
                                        } else {
                                            errorMessage = "Failed to link Gmail account. Please try again."
                                        }
                                    } else {
                                        errorMessage = "Invalid email or App Password. Please try again."
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text("Link Gmail")
                        }

                        Button(
                            onClick = onDismissRequest,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}
