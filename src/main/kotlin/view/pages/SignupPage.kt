package ca.uwaterloo.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.controller.SignUpController
import ca.uwaterloo.view.theme.AppTheme
import integration.SupabaseClient

@Composable
fun SignUpPage(NavigateToLogin: () -> Unit, NavigateToWelcomePage: () -> Unit, NavigateToHome: () -> Unit) {
    val scrollState = rememberScrollState()
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
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

                Spacer(modifier = Modifier.padding(20.dp))

                // Caption
                Row {
                    Text(
                        text = "Join ",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.secondary,
                        fontFamily = FontFamily.Default
                    )
                    Text(
                        text = "Snowmail!",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                        fontFamily = FontFamily.Default
                    )
                }

                Spacer(modifier = Modifier.padding(20.dp))

                Row(Modifier.fillMaxHeight()) { RegisterForm(NavigateToLogin, NavigateToHome) }
            }

        }
    }

}


@Composable
fun RegisterForm(NavigateToLogin: () -> Unit, NavigateToHome: () -> Unit) {
    val dbStorage = SupabaseClient()
    val signInController = SignUpController(dbStorage.authRepository)
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

        // Row {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 400.dp)
            ) {

                Row(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.06f)) {
                    Column(Modifier.fillMaxWidth(0.53f)) { Text("First Name", textAlign = TextAlign.Start, color = MaterialTheme.colors.primary) }
                    Column { Text(text = "Last Name", textAlign = TextAlign.End, color = MaterialTheme.colors.primary) }
                }

                var firstName by remember { mutableStateOf("") }
                var lastName by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var passwordConfirm by remember { mutableStateOf("") }

                Row(Modifier.fillMaxWidth()) {
                    // first name input
                    Column(Modifier.fillMaxWidth(0.48f)) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = MaterialTheme.colors.secondary,
                            )
                        )
                    }

                    Column(Modifier.fillMaxWidth(0.12f)) { Box{} }

                    // last name input
                    Column(Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = MaterialTheme.colors.secondary,
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Email Address
                Row { Text("Email Address", fontSize = 16.sp, color = MaterialTheme.colors.primary) }
                Spacer(modifier = Modifier.height(8.dp))
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
                Row {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = MaterialTheme.colors.secondary,
                        ),
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
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

                Spacer(modifier = Modifier.height(24.dp))

                Row { Text("Confirm Password", fontSize = 16.sp, color = MaterialTheme.colors.primary) }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    OutlinedTextField(
                        value = passwordConfirm,
                        onValueChange = { passwordConfirm = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = MaterialTheme.colors.secondary,
                        ),
                        trailingIcon = {
                            if (password == passwordConfirm && password.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Passwords match"
                                )
                            } else {
                                TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    if (confirmPasswordVisible) {
                                        Icon(
                                            painter = painterResource("VisibilityOff.svg"),
                                            contentDescription = "Hide password",
                                            tint = Color.Gray
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource("Visibility.svg"),
                                            contentDescription = "Show password",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    )
                }

                var errorMessage by remember { mutableStateOf("") }

                Spacer(modifier = Modifier.height(24.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    // register button
                    Button(onClick = {
                        // if first name is missing
                        if (firstName.isEmpty())  errorMessage = "Please fill in first name"
                        // if last name is missing
                        else if (lastName.isEmpty())  errorMessage = "Please fill in last name"
                        // if email is missing
                        else if (email.isEmpty())  errorMessage = "Please fill in email"
                        // if password is missing
                        else if (password.isEmpty())  errorMessage = "Please fill in password"
                        // if password is too short
                        else if (password.length < 6) errorMessage = "Password is too short"
                        // if password is not confirmed
                        else if (password != passwordConfirm) errorMessage = "Passwords do not match"

                        else {
                            val result = signInController.signUpUser(email, password, firstName, lastName)
                            result.onSuccess {
                                NavigateToHome()
                            }.onFailure { error ->
                                errorMessage = error.message ?: "Unknown error"
                            }
                        }


                    },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = Color.White
                        )) {
                        Text("Register")
                    }
                }


                // potential error message shown
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account?",
                        style = MaterialTheme.typography.body1.copy(fontSize = 14.sp, color = MaterialTheme.colors.onSecondary))
                    TextButton(onClick = { navigateLoginPage(NavigateToLogin) }) {
                        Text("Sign in", color = MaterialTheme.colors.primary, style = MaterialTheme.typography.body1.copy(fontSize = 14.sp))
                    }
                }

                Row(modifier = Modifier.fillMaxHeight(0.03f)) { Box{} }
                Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
                }

            }
            Column(Modifier.fillMaxWidth(0.3f)) { Box {} }
        }


// Navigate between Login and Signup pages
fun navigateLoginPage(NavigateToLogin: () -> Unit) {
    NavigateToLogin()
}




