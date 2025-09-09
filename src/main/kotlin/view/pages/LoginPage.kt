package ca.uwaterloo.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ca.uwaterloo.controller.SignInController
import ca.uwaterloo.view.theme.AppTheme
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking

object UserSession {
    var userId: String? = null
}

@Composable
fun loginPage(NavigateToSignup: () -> Unit, NavigateToWelcomePage: () -> Unit, NavigateToHome: () -> Unit) {
    val scrollState = rememberScrollState()
    AppTheme {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        )  {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .padding(50.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    Button(
                        onClick = NavigateToWelcomePage,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = MaterialTheme.colors.primary
                        )
                    ) {
                        Text("Back")
                    }
                }


                Spacer(modifier = Modifier.padding(50.dp))

                Row {
                    Text(
                        "Job Hunting's ",
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        "Tough",
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                Row {
                    Text(
                        "But You Don't Have To Do It ",
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.secondary,
                    )
                    Text(
                        "Alone!",
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                    )
                }

                Spacer(modifier = Modifier.padding(20.dp))
                Row(Modifier.fillMaxWidth()) { loginForm(NavigateToSignup, NavigateToHome) }
            }


        }
    }}


@Composable
fun loginForm(NavigateToSignup: () -> Unit, NavigateToHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row { loginWithAccount(NavigateToSignup, NavigateToHome) }
    }
}


@Composable
fun loginWithAccount(NavigateToSignup: () -> Unit, NavigateToHome: () -> Unit) {
    val dbStorage = SupabaseClient()
    val signInController = SignInController(dbStorage.authRepository)
    var passwordVisible by remember { mutableStateOf(false) }
    var showOtpLoginDialog by remember { mutableStateOf(false) }
    Column (
        modifier = Modifier.fillMaxWidth().padding(horizontal = 400.dp).padding(vertical = 15.dp)
    ) {

        // Email Address
        Row { Text("Email Address", fontSize = 16.sp, color = MaterialTheme.colors.primary) }
        Spacer(modifier = Modifier.height(8.dp))
        var email by remember { mutableStateOf("") }
        Row {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colors.secondary,
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Password
        Row { Text("Password", fontSize = 16.sp, color = MaterialTheme.colors.primary) }
        Spacer(modifier = Modifier.height(8.dp))
        var password by remember { mutableStateOf("") }
        Row {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colors.secondary,
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        if (passwordVisible) {
                            Icon(
                                painter = painterResource("VisibilityOff.svg"),
                                contentDescription = "Hide password",
                                tint = MaterialTheme.colors.secondary
                            )
                        } else {
                            Icon(
                                painter = painterResource("Visibility.svg"),
                                contentDescription = "Show password",
                                tint = MaterialTheme.colors.secondary
                            )
                        }
                    }
                }
            )
        }

        // potential error message shown
        var errorMessage by remember { mutableStateOf("") }
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign In Button
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    runBlocking {
                        val signInResult = signInController.signInUser(email, password)

                        signInResult.onSuccess { userId ->
                            // If sign-in succeeds, navigate to home page
                            UserSession.userId = userId
                            NavigateToHome()
                        }.onFailure { error ->
                            // Show error message if sign-in fails
                            errorMessage = error.message ?: "Login failed. Please try again."
                            email = ""
                            password = ""
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White,
                )
            ) {
                Text("Sign In")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Forgot your password?",
                style = MaterialTheme.typography.body1.copy(fontSize = 14.sp, color = MaterialTheme.colors.onSecondary))
            TextButton(onClick = { showOtpLoginDialog = true }) {
                Text("Sign in with OTP", color = MaterialTheme.colors.primary, style = MaterialTheme.typography.body1.copy(fontSize = 14.sp))
            }
        }

        if (showOtpLoginDialog) {
            signinWithOtpPage(
                onDismiss = { showOtpLoginDialog = false },
                NavigateToHome = NavigateToHome
            )
        }


        // Sign-up Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account?", style = MaterialTheme.typography.body1.copy(fontSize = 14.sp, color = MaterialTheme.colors.onSecondary))
            TextButton(onClick = { navigateLoginPage(NavigateToSignup) }) {
                Text("Sign up", color = MaterialTheme.colors.primary, style = MaterialTheme.typography.body1.copy(fontSize = 14.sp))
            }
        }
    }
}


@Composable
fun signinWithOtpPage(onDismiss: () -> Unit, NavigateToHome: () -> Unit) {
    val dbStorage = SupabaseClient()
    val signInController = SignInController(dbStorage.authRepository)

    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier.padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Sign in with Temporary Password",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                // Email input
                if (!isOtpSent) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            runBlocking {
                                val otpResult = signInController.sendOtpToEmail(email)
                                otpResult.onSuccess {
                                    isOtpSent = true
                                }.onFailure { error ->
                                    errorMessage = error.message ?: "Failed to send Temporary Password."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Send Temporary Password")
                    }
                } else {
                    // OTP input
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { otp = it },
                        label = { Text("Enter temporary password (check junk if not received)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            runBlocking {
                                val verifyResult = signInController.signInUserWithOTP(email, otp)
                                verifyResult.onSuccess { userId ->
                                    UserSession.userId = userId // Save the user ID to session
                                    NavigateToHome() // Navigate to home page
                                    onDismiss()
                                }.onFailure { error ->
                                    errorMessage = error.message ?: "Failed to verify temporary password."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Verify Temporary Password")
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        }
    }
}
