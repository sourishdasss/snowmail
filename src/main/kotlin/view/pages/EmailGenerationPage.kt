package ca.uwaterloo.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ca.uwaterloo.controller.ProfileController
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.WorkExperience
import ca.uwaterloo.service.ParserService
import ca.uwaterloo.view.components.TopNavigationBar
import ca.uwaterloo.view.components.DocumentSelectionDropdownButton
import ca.uwaterloo.view.components.EmailGenInputField
import ca.uwaterloo.view.components.EmailGenerationButton
import ca.uwaterloo.view.dialogs.GeneratedEmailAlertDialog
import ca.uwaterloo.view.theme.AppTheme
import controller.EmailGenerationController
import integration.OpenAIClient
import integration.SupabaseClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import model.UserInput
import model.UserProfile
import service.EmailGenerationService
import java.io.File


@Composable
fun EmailGenerationPage(
    userId: String,
    NavigateToDocuments: () -> Unit,
    NavigateToProfile: () -> Unit,
    NavigateToProgress: () -> Unit,
    NavigateToLogin: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var emailContent by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showGmailLinkPrompt by remember { mutableStateOf(false) }
    var selectedDocument by remember { mutableStateOf<String?>(null) }
    //  val coroutineScope = rememberCoroutineScope()
    val httpClient = HttpClient(CIO)
    val openAIClient = OpenAIClient(httpClient)

    // Create an instance of EmailGenerationService
    val parserService = ParserService(openAIClient)
    val emailGenerationService = EmailGenerationService(openAIClient, parserService)
    val emailGenerationController = EmailGenerationController(emailGenerationService)

    // user input values
    var companyInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var recruiterNameInput by remember { mutableStateOf("") }
    var recruiterEmailInput by remember { mutableStateOf("") }
    var jobtitleInput by remember { mutableStateOf("") }
    var userInput = UserInput(
        jobDescription = descriptionInput,
        recruiterEmail = recruiterEmailInput,
        jobTitle = jobtitleInput,
        company = companyInput,
        recruiterName = recruiterNameInput,
        fileURLs = listOf(selectedDocument ?: ""),
    )

    val resumeFile = selectedDocument?.let { File(it) }

    val dbStorage = SupabaseClient()
    val profileController = ProfileController(dbStorage.userProfileRepository)
    var gotName by remember { mutableStateOf("") }
    var gotSkills by remember { mutableStateOf(listOf<String>()) }
    var gotEducation by remember { mutableStateOf(listOf<EducationWithDegreeName>()) }
    var gotWorkExperience by remember { mutableStateOf(listOf<WorkExperience>()) }


    LaunchedEffect(Unit) {
        val getName = profileController.getUserName(userId)
        val getEducationResult = profileController.getEducation(userId)
        val getSkills = profileController.getSkills(userId)
        val getWorkExperienceResult = profileController.getWorkExperience(userId)

        getName.onSuccess { name ->
            gotName = name
        }.onFailure { error ->
            gotName = error.message ?: "Failed to retrieve user name"
        }
        getSkills.onSuccess { skills ->
            gotSkills = skills
        }.onFailure { error ->
            println("Error retrieving user skills: ${error.message}")
            gotSkills = emptyList()
        }
        getEducationResult.onSuccess { educationList ->
            gotEducation = educationList
        }.onFailure { error ->
            println("Error retrieving user education: ${error.message}")
            gotEducation = emptyList()
        }
        getWorkExperienceResult.onSuccess { workExperienceList ->
            gotWorkExperience = workExperienceList
        }.onFailure { error ->
            println("Error retrieving user work experience: ${error.message}")
            gotWorkExperience = emptyList()
        }
    }


    val userProfile = UserProfile(
        userId = userId,
        firstName = gotName,
        lastName = "",
        //skills = listOf("Java", "Kotlin", "SQL")
    )

    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
                    .background(MaterialTheme.colors.background)
            ) {
                TopNavigationBar(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { index ->
                        selectedTabIndex = index
                        when (index) {
                            0 -> {}
                            1 -> navigateOtherPage(NavigateToProgress)
                            2 -> navigateOtherPage(NavigateToDocuments)
                            3 -> navigateOtherPage(NavigateToProfile)
                        }
                    },
                    NavigateToLogin = NavigateToLogin
                )


                // Check Gmail account and app password using LaunchedEffect
                LaunchedEffect(Unit) {
                    val gmailAccountResult = profileController.getUserLinkedGmailAccount(userId)
                    val appPasswordResult = profileController.getUserGmailAppPassword(userId)

                    gmailAccountResult.onSuccess { gmailAccount ->
                        appPasswordResult.onSuccess { appPassword ->
                            if (gmailAccount.isEmpty() || appPassword.isEmpty()) {
                                showGmailLinkPrompt = true
                            }
                        }
                    }
                }

                // Conditionally show the prompt at the top of the page
                if (showGmailLinkPrompt) {
                    Text(
                        text = "* Please link your Gmail account on the Profile page before sending emails from this page.",
                        style = MaterialTheme.typography.body1,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(45.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            EmailGenInputField(
                                value = companyInput,
                                onValueChange = { companyInput = it },
                                label = "Company Name"
                            )
                            Spacer(modifier = Modifier.width(30.dp))
                            EmailGenInputField(
                                value = jobtitleInput,
                                onValueChange = { jobtitleInput = it },
                                label = "Job Title"
                            )

                        }
                        Spacer(modifier = Modifier.height(25.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            EmailGenInputField(
                                value = recruiterNameInput,
                                onValueChange = { recruiterNameInput = it },
                                label = "Recruiter Name"
                            )
                            Spacer(modifier = Modifier.width(30.dp))
                            EmailGenInputField(
                                value = recruiterEmailInput,
                                onValueChange = { recruiterEmailInput = it },
                                label = "Recruiter Email"
                            )
                        }

                        Spacer(modifier = Modifier.height(25.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Select a document for email generation",
                                    color = MaterialTheme.colors.secondary,
                                    modifier = Modifier.padding(bottom = 5.dp)
                                )
                                DocumentSelectionDropdownButton(
                                    userId = userId,
                                    selectedDocument = selectedDocument,
                                    onDocumentSelected = { document ->
                                        selectedDocument = document
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(25.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .shadow(8.dp, shape = RoundedCornerShape(15.dp))
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .padding(15.dp)
                    ) {
                        TextField(
                            value = descriptionInput,
                            onValueChange = { descriptionInput = it },
                            label = { Text("Job Description") },
                            modifier = Modifier
                                .width(700.dp)
                                .height(300.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    }
                }


                Spacer(modifier = Modifier.height(60.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Click a source to generate your email",
                            color = MaterialTheme.colors.secondary,
                            modifier = Modifier.padding(bottom = 15.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {

                            EmailGenerationButton(
                                emailGenerationController = emailGenerationController,
                                userInput = userInput,
                                userProfile = userProfile,
                                gotEducation = gotEducation,
                                gotWorkExperience = gotWorkExperience,
                                gotSkills = gotSkills,
                                resumeFile = resumeFile,
                                onEmailGenerated = { emailContent = it },
                                onShowDialog = { showDialog = it },
                                infoSource = "profile",
                                enabled = true,
                                userId = userId,
                                selectedDocument = selectedDocument
                            )

                            Spacer(modifier = Modifier.width(30.dp))

                            EmailGenerationButton(
                                emailGenerationController = emailGenerationController,
                                userInput = userInput,
                                userProfile = userProfile,
                                gotEducation = gotEducation,
                                gotWorkExperience = gotWorkExperience,
                                gotSkills = gotSkills,
                                resumeFile = resumeFile,
                                onEmailGenerated = { emailContent = it },
                                onShowDialog = { showDialog = it },
                                infoSource = "resume",
                                enabled = selectedDocument != null,
                                userId = userId,
                                selectedDocument = selectedDocument
                            )


                            if (showDialog) {
                                GeneratedEmailAlertDialog(
                                    userId = userId,
                                    onDismissRequest = { showDialog = false },
                                    title = "Generated Email",
                                    initialText = emailContent,
                                    reciepientAddress = recruiterEmailInput,
                                    jobTitle = jobtitleInput,
                                    companyName = companyInput,
                                    onConfirm = { newText ->
                                        emailContent = newText
                                        showDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            var currentPage by remember { mutableStateOf("") }
            EmailGenerationPage("a365a4c4-6427-4461-8cb4-2850fab8ac8c",{ currentPage = "profilePage"}, { currentPage = "profilePage"}, { currentPage = "profilePage"}, { currentPage = "login"})
        }
    }
}
