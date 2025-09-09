package ca.uwaterloo.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ca.uwaterloo.controller.ProfileController
import ca.uwaterloo.controller.ProgressController
import ca.uwaterloo.persistence.IJobApplicationRepository
import ca.uwaterloo.view.components.TopNavigationBar
import ca.uwaterloo.view.theme.AppTheme
import integration.OpenAIClient
import integration.SupabaseClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import service.email
import ca.uwaterloo.view.dialogs.EmailDialog

@Composable
fun JobProgressPage(
    userId: String,
    NavigateToDocuments: () -> Unit,
    NavigateToProfile: () -> Unit,
    NavigateToEmialGen: () -> Unit,
    NavigateToLogin: () -> Unit
) {
    val dbStorage = SupabaseClient()
    val openAIClient = OpenAIClient(HttpClient(CIO))
    val progressController = ProgressController(dbStorage.jobApplicationRepository, openAIClient)
    val profileController = ProfileController(dbStorage.userProfileRepository)

    var selectedTabIndex by remember { mutableStateOf(1) }
    var showDialog by remember { mutableStateOf(false) }
    var emailIndex by remember { mutableStateOf(0) }
    var emails by remember { mutableStateOf<List<email>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var progress by remember { mutableStateOf<IJobApplicationRepository.Progress?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var refreshTrigger by remember { mutableStateOf(false) }
    var linkedEmail by remember { mutableStateOf<String>("") }
    var appPassword by remember { mutableStateOf<String>("") }

    LaunchedEffect(userId, refreshTrigger) {
        isLoading = true
        val getLinkedEmailResult = profileController.getUserLinkedGmailAccount(userId)
        val getAppPasswardResult = profileController.getUserGmailAppPassword(userId)
        try {

            getLinkedEmailResult.onSuccess { result ->
                linkedEmail = result
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve user linked email"
            }

            getAppPasswardResult.onSuccess { result ->
                appPassword = result
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve user app password"
            }

            val getEmailResult = runCatching {
                progressController.getNewEmails(
                    userId,
                    linkedEmail,
                    appPassword,
                    dbStorage.documentRepository
                )
            }
            val getProgressResult = runCatching { progressController.getProgress(userId) }

            getEmailResult.onSuccess { emailList ->
                emails = emailList
                if (emails.isNotEmpty()) showDialog = true
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve user emails"
            }

            getProgressResult.onSuccess { result ->
                progress = result
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve user progress"
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "An unexpected error occurred"
        } finally {
            isLoading = false // Always reset `isLoading` in the `finally` block
        }
    }

    if (isLoading) {
        Dialog(onDismissRequest = { isLoading = false }) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colors.surface,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Please wait while we check for any new emails related to your job applications. This process may take 10-60 seconds.",
                            color = MaterialTheme.colors.secondary
                        )
                    }
                }
            }
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
                    .background(Color(0xFFF8FAFC))
            ) {

                TopNavigationBar(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { index ->
                        selectedTabIndex = index
                        when (index) {
                            0 -> NavigateToEmialGen()
                            1 -> {}
                            2 -> NavigateToDocuments()
                            3 -> NavigateToProfile()
                        }
                    },
                    NavigateToLogin = NavigateToLogin
                )

                Spacer(modifier = Modifier.height(16.dp))


                if (isLoading || progress == null) {
                    JobStatusColumnsPlaceholder()
                } else {
                    JobStatusColumns(progress!!)
                }

                if (showDialog && emails.isNotEmpty()) {
                    EmailDialog(
                        emails = emails,
                        emailIndex = emailIndex,
                        userId = userId,
                        progressController = progressController,
                        onNextEmail = { emailIndex = (emailIndex + 1) % emails.size },
                        documentRepository = dbStorage.documentRepository,
                        onClose = {
                            showDialog = false
                            emailIndex = 0
                            refreshTrigger = !refreshTrigger
                        }
                    )
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun JobStatusColumns(progress: IJobApplicationRepository.Progress) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        JobStatusColumn("APPLIED", progress.appliedItemCount, progress.appliedJobs, modifier = Modifier.weight(1f))
        JobStatusColumn("INTERVIEWING", progress.interviewedItemCount, progress.interviewedJobs, modifier = Modifier.weight(1f))
        JobStatusColumn("OFFER", progress.offerItemCount, progress.offerJobs, modifier = Modifier.weight(1f))
        JobStatusColumn("OTHER", progress.otherItemCount, progress.otherJobs, modifier = Modifier.weight(1f))
    }
}

@Composable
fun JobStatusColumnsPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        JobStatusColumnPlaceholder("APPLIED", modifier = Modifier.weight(1f))
        JobStatusColumnPlaceholder("INTERVIEWING", modifier = Modifier.weight(1f))
        JobStatusColumnPlaceholder("OFFER", modifier = Modifier.weight(1f))
        JobStatusColumnPlaceholder("OTHER", modifier = Modifier.weight(1f))
    }
}

@Composable
fun JobStatusColumnPlaceholder(title: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "$title (0)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFFD3D3D3), shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading...",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}



@Composable
fun JobStatusColumn(
    title: String,
    itemCount: Int,
    jobs: List<IJobApplicationRepository.JobProgress>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {

        Text(
            text = "$title ($itemCount)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            if (jobs.isNotEmpty()) {

                items(jobs) { job ->
                    JobCard(
                        position = job.jobTitle,
                        company = job.companyName,
                        recruiterEmail = job.recruiterEmail
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No applications",
                            color = MaterialTheme.colors.secondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun JobCard(position: String, company: String, recruiterEmail: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = position, fontWeight = FontWeight.Bold, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Company Icon",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = company, color = MaterialTheme.colors.secondary, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))


        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = "Email Icon",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = recruiterEmail, color = MaterialTheme.colors.secondary, fontSize = 12.sp)
        }
    }
}
