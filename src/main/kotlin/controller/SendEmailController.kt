package controller

import ca.uwaterloo.controller.DocumentController
import ca.uwaterloo.persistence.IDocumentRepository
import ca.uwaterloo.persistence.IJobApplicationRepository
import model.email
import service.sendEmail

// function send_email returns string SUCCESS if email is sent successfully
// otherwise, return Error message, please show it to users
// Error type 1: Missing Gmail Account or Password, please go to profile page and finish linking gmail account
// Error type 2: Failed to send the email. Please verify that your linked email address and password are correct.

class SendEmailController(private val jobApplicationRepository: IJobApplicationRepository,
                          private val documentRepository: IDocumentRepository) {
    suspend fun send_email(
        recipient: String,
        subject: String,
        text: String,
        // Modified Parameters from Sprint 3
        buckets: List<String>,
        documentsType: List<String>,
        documentsName: List<String>,
        // ---------
        userID: String,
        jobTitle: String,
        companyName: String

    ): String {
        val fileURLs = mutableListOf<String>()
        for (i in documentsType.indices) {
            val bucket = buckets[i]
            val documentType = documentsType[i]
            val documentName = documentsName[i]
            val documentController = DocumentController(documentRepository)
            val signedUrl = documentController.viewDocument(bucket, userID, documentType, documentName).getOrNull()!!
            fileURLs.add(signedUrl)
        }
        val senderEmail = jobApplicationRepository.getGmailAccount(userID).getOrNull()
        val password = jobApplicationRepository.getGmailPassword(userID).getOrNull()
        if (senderEmail == "" || password == "") {
            return "Missing Gmail Account or Password, please go to profile page and finish linking gmail account"
        }
        if (senderEmail == null || password == null) {
            return "Missing Gmail Account or Password, please go to profile page and finish linking gmail account"
        }
        val Email = email(senderEmail, password, recipient, subject, text, fileURLs, documentsName)

        if (!sendEmail(Email)) {
            return "Failed to send the email. Please verify that your linked email address and password are correct."
        }

        // update last refresh time if necessary
        jobApplicationRepository.initializeRefreshTime(userID)

        // save job application
        jobApplicationRepository.createJobApplication(
            userID,
            jobTitle,
            companyName,
            recipient
        )
        return "Success"

    }
}










