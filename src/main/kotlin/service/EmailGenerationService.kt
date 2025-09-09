package service

import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.PersonalProject
import ca.uwaterloo.model.WorkExperience
import ca.uwaterloo.service.ParserService
import integration.OpenAIClient

import model.UserInput
import model.GeneratedEmail
import model.UserProfile
import java.io.File

class EmailGenerationService(private val openAIClient: OpenAIClient, private val parserService: ParserService) {

     suspend fun generateEmailFromResume(userInput: UserInput, userProfile: UserProfile, userResume: File): GeneratedEmail? {
          println("entered generateEmailFromResume")
          val cleanedInput = cleanInput(userInput)
          //print out the actual type of userResume
          println("userResume type: ${userResume::class.simpleName}")
          val resumeText = parserService.extractTextFromPDF(userResume)
          println("extractText successful")
          println("resumeFile: ${userResume?.path ?: "No file provided"}")
          return try {
               parserService.parseEmailContent(openAIClient.generateEmailFromResume(userInput, userProfile, resumeText))
          } catch (e: Exception) {
               println("try block failed")
               throw RuntimeException("Failed to generate email: ${e.message}")
          }
     }

     suspend fun generateEmailFromProfile(userInput: UserInput, userProfile: UserProfile, education: List<EducationWithDegreeName>, workExperience: List<WorkExperience>, projects: List<PersonalProject>, skills: List<String>): GeneratedEmail? {
          val cleanedInput = cleanInput(userInput)

          return try {
               parserService.parseEmailContent(openAIClient.generateEmailFromProfile(userInput, userProfile, education, workExperience, projects, skills))
          } catch (e: Exception) {
               throw RuntimeException("Failed to generate email: ${e.message}")
          }
     }

     private fun cleanInput(userInput: UserInput): UserInput {
          // Implement input cleaning logic here
          return userInput
     }

}
