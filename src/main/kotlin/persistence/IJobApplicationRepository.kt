package ca.uwaterloo.persistence

import model.JobApplication
import java.util.*


interface IJobApplicationRepository {

    data class Progress(
        val appliedItemCount: Int,
        val interviewedItemCount: Int,
        val offerItemCount: Int,
        val otherItemCount: Int,
        val appliedJobs: List<JobProgress> ,
        val interviewedJobs: List<JobProgress>,
        val offerJobs: List<JobProgress>,
        val otherJobs: List<JobProgress>
    )

    data class JobProgress(
        val jobTitle: String,
        val companyName: String,
        val recruiterEmail: String
    )


    // we need:
    suspend fun createJobApplication(
        userId: String,
        jobTitle: String,
        companyName: String,
        recruiterEmail: String): Result<Boolean>

    suspend fun createRecruiter(
        recruiterEmail: String): Int?

    suspend fun updateRefreshTime(
        userID: String
    ): Result<Boolean>

    suspend fun getRefreshTime(
        userID: String
    ): Result<Date>

    suspend fun updateJobApplicationStatus(
        jobApplicationID: String,
        statusID: Int
    ): Result<Boolean>

    suspend fun getJobWithStatus(
        userId: String,
        statusID: Int
    ): Result<List<JobApplication>>

    suspend fun getProgress(
        userId: String,
    ): Result<Progress>

    suspend fun getRecruiterEmail(
        recruiterEmailID: Int
    ): Result<String>

    suspend fun getAppliedJobs(
        userId: String
    ): Result<List<Pair<JobProgress, String>>> // JobProgress, JobApplicationID

    suspend fun getAllRecruiterEmailAddress(
        userId: String
    ): List<String>

    suspend fun initializeRefreshTime(
        userId: String
    ): Result<Boolean>

    suspend fun getGmailPassword(
        userId: String
    ): Result<String?>

    suspend fun getGmailAccount(
        userId: String
    ): Result<String?>

    suspend fun getCorrespondJobs(
        userId: String,
        email: String
    ): Result<List<Pair<JobProgress, String>>> // JobProgress, JobApplicationID

    suspend fun getRecruiterID(
        recruiterEmail: String
    ): Result<Int>



}

