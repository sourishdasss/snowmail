package ca.uwaterloo.view.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ca.uwaterloo.controller.DocumentController
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.WorkExperience
import controller.EmailGenerationController
import integration.SupabaseClient
import model.GeneratedEmail
import model.UserInput
import model.UserProfile
import java.io.File
import kotlinx.coroutines.runBlocking

@Composable
fun EmailGenerationButton(
    emailGenerationController: EmailGenerationController,
    userInput: UserInput,
    userProfile: UserProfile,
    gotEducation: List<EducationWithDegreeName>,
    gotWorkExperience: List<WorkExperience>,
    gotSkills: List<String>,
    resumeFile: File?,
    onEmailGenerated: (String) -> Unit,
    onShowDialog: (Boolean) -> Unit,
    infoSource: String,
    enabled: Boolean,
    userId: String,
    selectedDocument: String?
) {
    var emailContent by remember { mutableStateOf("") }
    var rFile by remember { mutableStateOf<File?>(null) }

    Button(
        onClick = {
            runBlocking {
                try {
                    if (infoSource == "profile") {
                        val generatedEmail: GeneratedEmail? = emailGenerationController.generateEmail(
                            informationSource = "profile",
                            userInput = userInput,
                            userProfile = userProfile,
                            education = gotEducation,
                            workExperience = gotWorkExperience,
                            skills = gotSkills
                        )

                        println("Generated Email: ${generatedEmail?.body}")
                        emailContent = generatedEmail?.body ?: "Failed to generate email"
                        onEmailGenerated(emailContent)
                        onShowDialog(true)
                    } else {
                        // download resume file using documentController
                        val documentController = DocumentController(SupabaseClient().documentRepository)
                        val resumeResult = documentController.getDocumentAsFile("user_documents", userId, "Resume", selectedDocument ?: "")
                        resumeResult.onSuccess { file ->
                            rFile = file
                        }.onFailure { error ->
                            println("Error downloading resume: ${error.message}")
                            return@runBlocking
                        }

                        val generatedEmail: GeneratedEmail? = emailGenerationController.generateEmail(
                            informationSource = "resume",
                            userInput = userInput,
                            userProfile = userProfile,
                            education = gotEducation,
                            workExperience = gotWorkExperience,
                            skills = gotSkills,
                            resumeFile = rFile
                        )

                        println("Generated Email: ${generatedEmail?.body}")
                        emailContent = generatedEmail?.body ?: "Failed to generate email"
                        onEmailGenerated(emailContent)
                        onShowDialog(true)
                    }
                } catch (e: Exception) {
                    println("Email generation button try failed, Error: ${e.message}")
                }
            }
        },
        modifier = Modifier
            .height(40.dp)
            .width(230.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = Color.White
        ),
        enabled = enabled
    ) {
        Text(if (infoSource == "profile") "Profile information" else "Selected Resume")
    }
}
