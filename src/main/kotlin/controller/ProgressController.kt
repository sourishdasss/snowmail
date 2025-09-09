package  ca.uwaterloo.controller

import ca.uwaterloo.persistence.DocumentRepository
import ca.uwaterloo.persistence.IJobApplicationRepository
import ca.uwaterloo.persistence.IJobApplicationRepository.JobProgress
import ca.uwaterloo.persistence.IJobApplicationRepository.Progress
import integration.OpenAIClient
import service.email
import service.searchEmails

class ProgressController(private val jobApplicationRepository: IJobApplicationRepository, private val openAIClient: OpenAIClient) {

    // getProgress function for listing all information Progress page needs
    // what is returned in this function?
    // a struct Progress

    // what is in Progress?
    // data class Progress(
    //        val appliedItemCount: Int,
    //        val interviewedItemCount: Int,
    //        val offerItemCount: Int,
    //        val otherItemCount: Int,
    //        val appliedJobs: List<JobProgress> ,
    //        val interviewedJobs: List<JobProgress>,
    //        val offerJobs: List<JobProgress>,
    //        val otherJobs: List<JobProgress>
    //    )

    // so let result = getProgress(userID), example:
    // number of applied jobs = result.appliedItemCount
    // jobs information of applied jobs:
    // for job in result.appliedJobs:
    //     jobTitle = job.jobTitle
    //     companyName = job.companyName
    //     recruiterEmail = job.recruiterEmail

    suspend fun getProgress(userId: String): Progress {
        val progress = jobApplicationRepository.getProgress(userId).getOrNull()!!
        jobApplicationRepository.updateRefreshTime(userId)
        return progress
    }

   // Determine the application status of the application using an LLM
    suspend fun classifyApplicationStatus(emailContent: String): String {
        return openAIClient.classifyStatusOfApplication(emailContent)
    }



    // return all new emails from last refresh time to now
    // return value is List<Email>
    suspend fun getNewEmails(userId: String, linkedEmail: String, appPassword: String, documentRepository: DocumentRepository): List<email> {
        // get all recruiter emails
        val recruiterEmails = jobApplicationRepository.getAllRecruiterEmailAddress(userId)
        // get last refresh time
        val lastRefreshTime = jobApplicationRepository.getRefreshTime(userId).getOrNull()!!
        // TO BE ADDED: REMOVE HARDCODE EMAIL AND GMAIL PASSWORD
        // search emails
        val emails = searchEmails(linkedEmail, appPassword, lastRefreshTime, recruiterEmails, documentRepository)
        return emails
    }


    // return all jobs with APPLIED status
    // intended to be called when prompting user to decide which job application status to change
    suspend fun getAllAppliedJobs(userId: String): List<Pair<JobProgress, String>> {
        return jobApplicationRepository.getAppliedJobs(userId).getOrNull()!!
    }

    // return all jobs with CORRESPONDING email address
    suspend fun getCorrespondJobs(userId: String, email: String): List<Pair<JobProgress, String>> {
        return jobApplicationRepository.getCorrespondJobs(userId, email).getOrNull()!!
    }


    // modifyStatus function for changing the status of a job application
    suspend fun modifyStatus(JobApplicationID: String, newStatus: Int) {
        jobApplicationRepository.updateJobApplicationStatus(JobApplicationID, newStatus)
    }

    suspend fun deleteAttachments(files: List<String>, documentRepository: DocumentRepository) {
        documentRepository.deleteAttachments(files)
    }

}




