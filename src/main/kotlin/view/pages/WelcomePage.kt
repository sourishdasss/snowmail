package ca.uwaterloo.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ca.uwaterloo.service.ParserService
import ca.uwaterloo.view.theme.AppTheme
import model.GeneratedEmail
import controller.EmailGenerationController
import integration.OpenAIClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import model.UserInput
import model.UserProfile
import service.EmailGenerationService
import kotlinx.coroutines.runBlocking

@Composable
fun WelcomePage(
    NavigateToSignup: () -> Unit,
    NavigateToLogin: () -> Unit,
    NavigateToWelcomePage1: () -> Unit
) {
    AppTheme {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .padding(50.dp)
            ) {
                Column (
                    modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.50f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(300.dp))
                    Text(
                        text = "Find your dream job with our help",
                        fontWeight = FontWeight.Bold,
                        fontSize = 55.sp,
                        color = MaterialTheme.colors.secondary,
                        style = TextStyle(lineHeight = 70.sp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "No longer spend hours writing emails to recruiters, instead spend that time on your personal development",
                        style = MaterialTheme.typography.body1.copy(fontSize = 20.sp),
                        color = MaterialTheme.colors.onSecondary
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Button(
                        onClick = NavigateToWelcomePage1,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Learn More")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Column (
                    modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, end = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.width(80.dp))
                        Button(
                            onClick = NavigateToLogin,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
                                contentColor = MaterialTheme.colors.primary,
                            )
                        ) {
                            Text("Log in")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = NavigateToSignup,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Sign Up")
                        }
                    }

                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                    ) {
                        val httpClient = HttpClient(CIO)
                        val openAIClient = OpenAIClient(httpClient)
                        val parserService = ParserService(openAIClient)
                        val emailGenerationService = EmailGenerationService(openAIClient, parserService)
                        val emailGenerationController = EmailGenerationController(emailGenerationService)
                        var emailtext by remember { mutableStateOf("Email") }


                        var userInput = UserInput(
                            jobDescription = "",
                            recruiterEmail = "",
                            jobTitle = "",
                            company = "",
                            recruiterName = "recruiting agent",
                            fileURLs = listOf(""),
                        )

                        val userProfile = UserProfile(
                            userId = "0",
                            firstName = "",
                            lastName = "",
                        )

                        TextField(
                            value = emailtext,
                            onValueChange = {   emailtext = it },
                            label = {
                                Text(
                                    "Test it out!",
                                    style = TextStyle(fontSize = 20.sp, color = MaterialTheme.colors.primary),
                                    modifier = Modifier.padding(top = 16.dp, bottom = 20.dp)
                                ) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(530.dp)
                                .padding(16.dp)
                                .padding(top = 32.dp)
                                .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )

                        Button (
                            onClick = {
                                runBlocking {
                                    val generatedEmail: GeneratedEmail? = emailGenerationController.generateEmail(
                                        informationSource = "profile",
                                        userInput = userInput,
                                        userProfile = userProfile,
                                        education = emptyList(),
                                        workExperience = emptyList(),
                                        skills = emptyList(),
                                        resumeFile = null
                                    )
                                    emailtext = generatedEmail?.body ?: "Failed to generate email"
                                }

                            },
                            modifier = Modifier
                                .width(200.dp)
                                .height(75.dp)
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                "Generate Email",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
    }
}
