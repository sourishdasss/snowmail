package ca.uwaterloo.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.controller.ProfileController
import ca.uwaterloo.controller.SignInController
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.PersonalProject
import ca.uwaterloo.model.WorkExperience
import ca.uwaterloo.view.components.TopNavigationBar
import ca.uwaterloo.view.components.EducationSection
import ca.uwaterloo.view.components.ProjectSection
import ca.uwaterloo.view.components.SkillsSection
import ca.uwaterloo.view.components.WorkExperienceSection
import ca.uwaterloo.view.dialogs.EditContactDialog
import ca.uwaterloo.view.dialogs.EditPortfolioDialog
import ca.uwaterloo.view.dialogs.GmailLinkingDialog
import ca.uwaterloo.view.theme.AppTheme
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking

@Composable
fun ProfilePage(
    userId: String,
    NavigateToDocuments: () -> Unit,
    NavigateToEmailGen: () -> Unit,
    NavigateToProgress: () -> Unit,
    NavigateToLogin: () -> Unit
) {
    // Initialize the controller
    val dbStorage = SupabaseClient()
    val profileController = ProfileController(dbStorage.userProfileRepository)

    // State variables for dialog visibility
    var showEducationDialog by remember { mutableStateOf(false) }
    var showExperienceDialog by remember { mutableStateOf(false) }
    var showSkillsDialog by remember { mutableStateOf(false) }
    var EditContactDialog by remember { mutableStateOf(false) }
    var showProjectDialog by remember { mutableStateOf(false) }
    var showEditProjectDialog by remember { mutableStateOf(false) }
    var showEditEducationDialog by remember { mutableStateOf(false) }
    var showEditExperienceDialog by remember { mutableStateOf(false) }
    var showEditPortfolioDialog by remember { mutableStateOf(false) }

    // State variables for user profile
    var userName by remember { mutableStateOf("") }
    var educationList by remember { mutableStateOf<List<EducationWithDegreeName>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userLocation by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf(listOf<String>()) }
    var userLinkedIn by remember { mutableStateOf("") }
    var userGithub by remember { mutableStateOf("") }
    var userPersonalWebsite by remember { mutableStateOf("") }
    var selectedEducation by remember { mutableStateOf<EducationWithDegreeName?>(null) }
    var selectedExperience by remember { mutableStateOf<WorkExperience?>(null) }
    var workExperienceList by remember { mutableStateOf<List<WorkExperience>>(emptyList()) }
    var projectList by remember { mutableStateOf<List<PersonalProject>>(emptyList()) }
    var selectedProject by remember { mutableStateOf<PersonalProject?>(null) }
    var showGmailLinkingDialog by remember { mutableStateOf(false) }

    // State variables for tab navigation
    var currentPage by remember { mutableStateOf("ProfilePage") }
    var selectedTabIndex by remember { mutableStateOf(3) }

    // Functions to refresh the user profile
    fun refreshEducationList() {
        runBlocking {
            val educationResult = profileController.getEducation(userId)
            educationResult.onSuccess { educationRecords ->
                educationList = educationRecords
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve education records"
            }
        }
    }

    fun refreshWorkExperienceList() {
        runBlocking {
            val result = profileController.getWorkExperience(userId)
            result.onSuccess { experiences ->
                workExperienceList = experiences
            }
        }
    }

    fun refreshProjectList() {
        runBlocking {
            val result = profileController.getProjects(userId)
            result.onSuccess { projects ->
                projectList = projects
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve projects"
            }
        }
    }

    fun refreshContactInfo() {
        runBlocking {
            val locationResult = profileController.getUserCity(userId)
            val phoneResult = profileController.getUserPhone(userId)

            locationResult.onSuccess { city ->
                userLocation = city
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve updated location."
            }

            phoneResult.onSuccess { updatedPhone ->
                userPhone = updatedPhone
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve updated phone."
            }
        }
    }

    fun refreshPortfolioInfo() {
        runBlocking {
            val linkedInResult = profileController.getUserLinkedIn(userId)
            val githubResult = profileController.getUserGithub(userId)
            val websiteResult = profileController.getUserPersonalWebsite(userId)

            linkedInResult.onSuccess { linkedIn ->
                userLinkedIn = linkedIn
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve updated LinkedIn URL."
            }

            githubResult.onSuccess { github ->
                userGithub = github
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve updated GitHub URL."
            }

            websiteResult.onSuccess { website ->
                userPersonalWebsite = website
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve updated personal website."
            }
        }
    }

    fun refreshSkills() {
        runBlocking {
            val getSkillsResult = profileController.getSkills(userId)
            getSkillsResult.onSuccess { loadedSkills ->
                skills = loadedSkills
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to load skills"
            }
        }
    }

    LaunchedEffect(userId) {
        val getNameResult = profileController.getUserName(userId)
        val educationResult = profileController.getEducation(userId)
        val experienceResult = profileController.getWorkExperience(userId)
        val getEmailResult = profileController.getUserEmail(userId)
        val getLocationResult = profileController.getUserCity(userId)
        val getPhoneResult = profileController.getUserPhone(userId)
        val getSkillsResult = profileController.getSkills(userId)
        val getLinkedInResult = profileController.getUserLinkedIn(userId)
        val getGithubResult = profileController.getUserGithub(userId)
        val getWebsiteResult = profileController.getUserPersonalWebsite(userId)
        val projectResult = profileController.getProjects(userId)

        projectResult.onSuccess { projects ->
            projectList = projects
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve projects"
        }

        getNameResult.onSuccess { name ->
            userName = name
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve user name"
        }

        educationResult.onSuccess { educationRecords ->
            educationList = educationRecords
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve education records"
        }

        experienceResult.onSuccess { experiences ->
            workExperienceList = experiences
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve work experience records"
        }

        getEmailResult.onSuccess { email ->
            userEmail = email
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve user email"
        }

        getLocationResult.onSuccess { city ->
            userLocation = city
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve user location"
        }


        getPhoneResult.onSuccess { phone ->
            userPhone = phone
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve user phone"
        }

        getSkillsResult.onSuccess { fetchedSkills ->
            skills = fetchedSkills
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to load skills"
        }

        getLinkedInResult.onSuccess { linkedIn ->
            userLinkedIn = linkedIn
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve LinkedIn URL"
        }

        getGithubResult.onSuccess { github ->
            userGithub = github
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve GitHub URL"
        }

        getWebsiteResult.onSuccess { website ->
            userPersonalWebsite = website
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve personal website"
        }
    }

    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(MaterialTheme.colors.background)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TopNavigationBar(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { index ->
                        selectedTabIndex = index
                        when (index) {
                            0 -> navigateOtherPage(NavigateToEmailGen)
                            1 -> navigateOtherPage(NavigateToProgress)
                            2 -> navigateOtherPage(NavigateToDocuments)
                            3 -> {}
                        }
                    },
                    NavigateToLogin = NavigateToLogin
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Name
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier.padding(10.dp)
                        )
                    } else {
                        Text(
                            text = userName.ifEmpty { "Loading..." },
                            fontSize = 24.sp,
                            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.primary),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(10.dp)
                        .shadow(8.dp, shape = RoundedCornerShape(15.dp))
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.White)
                        .padding(8.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showGmailLinkingDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.primary,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Gmail Linking")
                            }

                            if (showGmailLinkingDialog) {
                                GmailLinkingDialog(
                                    onDismissRequest = { showGmailLinkingDialog = false },
                                    userId = userId,
                                    profileController = profileController
                                )
                            }
                            Spacer(modifier = Modifier.weight(0.1f))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        SectionTitle("Contact Information")
                        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                ProfileDetail(label = "Email Address:", value = userEmail)
                                ProfileDetail(label = "Location:", value = userLocation ?: "Location not available")
                                ProfileDetail(label = "Phone: +1 ", value = userPhone ?: "Phone not available")
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = { EditContactDialog = true },
                                    modifier = Modifier.size(15.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Contact",
                                        tint = MaterialTheme.colors.primary
                                    )
                                }
                            }
                        }

                        if (EditContactDialog) {
                            EditContactDialog(
                                userId = userId,
                                profileController = profileController,
                                userLocation = userLocation,
                                userPhone = userPhone,
                                onDismiss = { EditContactDialog = false },
                                onContactUpdated = {
                                    refreshContactInfo()
                                    EditContactDialog = false
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        SectionTitle("Portfolio")
                        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            if (userLinkedIn.isEmpty() && userGithub.isEmpty() && userPersonalWebsite.isEmpty()) {
                                Text(
                                    text = "No portfolio links added",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colors.secondary,
                                    modifier = Modifier.padding(8.dp)
                                )
                            } else {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    if (userLinkedIn.isNotEmpty()) {
                                        ProfileDetail(label = "LinkedIn URL:", value = userLinkedIn)
                                    }
                                    if (userGithub.isNotEmpty()) {
                                        ProfileDetail(label = "GitHub URL:", value = userGithub)
                                    }
                                    if (userPersonalWebsite.isNotEmpty()) {
                                        ProfileDetail(label = "Portfolio URL:", value = userPersonalWebsite)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = { showEditPortfolioDialog = true },
                                    modifier = Modifier.size(15.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Portfolio & Socials",
                                        tint = MaterialTheme.colors.primary
                                    )
                                }
                            }
                        }

                        if (showEditPortfolioDialog) {
                            EditPortfolioDialog(
                                userId = userId,
                                profileController = profileController,
                                linkedInUrl = userLinkedIn,
                                githubUrl = userGithub,
                                portfolioUrl = userPersonalWebsite,
                                onDismiss = { showEditPortfolioDialog = false },
                                onLinksUpdated = {
                                    refreshPortfolioInfo()
                                    showEditPortfolioDialog = false
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        EducationSection(
                            userId = userId,
                            profileController = profileController,
                            educationList = educationList,
                            showEducationDialog = showEducationDialog,
                            showEditEducationDialog = showEditEducationDialog,
                            selectedEducation = selectedEducation,
                            onEducationAdded = { refreshEducationList() },
                            onEducationEdited = { refreshEducationList() },
                            onEducationDeleted = { refreshEducationList() },
                            onShowEducationDialogChange = { showEducationDialog = it },
                            onShowEditEducationDialogChange = { showEditEducationDialog = it },
                            onSelectedEducationChange = { selectedEducation = it }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        WorkExperienceSection(
                            userId = userId,
                            profileController = profileController,
                            workExperienceList = workExperienceList,
                            showWorkExperienceDialog = showExperienceDialog,
                            showEditWorkExperienceDialog = showEditExperienceDialog,
                            selectedWorkExperience = selectedExperience,
                            onWorkExperienceAdded = { refreshWorkExperienceList() },
                            onWorkExperienceEdited = { refreshWorkExperienceList() },
                            onWorkExperienceDeleted = { refreshWorkExperienceList() },
                            onShowWorkExperienceDialogChange = { showExperienceDialog = it },
                            onShowEditWorkExperienceDialogChange = { showEditExperienceDialog = it },
                            onSelectedWorkExperienceChange = { selectedExperience = it }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ProjectSection(
                            userId = userId,
                            profileController = profileController,
                            projectList = projectList,
                            showProjectDialog = showProjectDialog,
                            showEditProjectDialog = showEditProjectDialog,
                            selectedProject = selectedProject,
                            onProjectAdded = { refreshProjectList() },
                            onProjectEdited = { refreshProjectList() },
                            onProjectDeleted = { refreshProjectList() },
                            onShowProjectDialogChange = { showProjectDialog = it },
                            onShowEditProjectDialogChange = { showEditProjectDialog = it },
                            onSelectedProjectChange = { selectedProject = it }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SkillsSection(
                            userId = userId,
                            profileController = profileController,
                            skills = skills,
                            showSkillsDialog = showSkillsDialog,
                            onSkillsAdded = { refreshSkills() },
                            onSkillsDeleted = { refreshSkills() },
                            onShowSkillsDialogChange = { showSkillsDialog = it }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


fun navigateOtherPage(NavigateOtherPage: () -> Unit) {
    NavigateOtherPage()
}


//This is the style for section titles
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 16.dp, bottom = 8.dp),
        textAlign = TextAlign.Start
    )
}

// This is the style for profile details
@Composable
fun ProfileDetail(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row {
            Text(
                "$label ",
                fontWeight = FontWeight.Bold,
                style = TextStyle(fontSize = 14.sp, color = Color.Black)
            )
            Text(
                value,
                style = TextStyle(fontSize = 14.sp, color = MaterialTheme.colors.secondary)
            )
        }
    }
}


