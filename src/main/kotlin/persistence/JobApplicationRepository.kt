package persistence

import ca.uwaterloo.persistence.IJobApplicationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import model.JobApplication
import model.Recruiter
import java.time.LocalDateTime
import java.time.ZoneId


class JobApplicationRepository(private val supabase: SupabaseClient) : IJobApplicationRepository {
    override suspend fun createJobApplication(
        userId: String,
        jobTitle: String,
        companyName: String,
        recruiterEmail: String
    ): Result<Boolean> {
        return try {
            val jobApplication = JobApplication(
                userId = userId,
                jobTitle = jobTitle,
                companyName = companyName,
                recruiterEmailID = createRecruiter(recruiterEmail)!!,
                appStatusId = 1
            )
            supabase.from("job_application_detail").insert(jobApplication)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to create job application"))
        }
    }

    override suspend fun createRecruiter(recruiterEmail: String): Int? {
        try {
            // check if the email already exists
            val existingEmail = supabase.from("recruiter").select() {
                filter {
                    eq("email", recruiterEmail)
                }
            }.decodeList<Recruiter>()

            // if the skill already exists
            if (existingEmail.isNotEmpty()) {
                return existingEmail[0].recruiterID
            }

            // insert and return
            val result = supabase.from("recruiter").insert(mapOf("email" to recruiterEmail)) {
                select()
            }.decodeSingle<Recruiter>()
            return result.recruiterID

        } catch (e: Exception) {
            throw Exception("Failed to create recruiter")
        }

    }


    override suspend fun updateRefreshTime(userID: String): Result<Boolean> {
        return try {
            supabase.from("user_profile").update(
                mapOf("last_email_refresh_time" to java.time.LocalDateTime.now().toString())
            ) {
                filter {
                    eq("user_id", userID)
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            println(e)
            Result.failure(Exception("Failed to update refresh time"))
        }
    }

    override suspend fun getRefreshTime(userID: String): Result<java.util.Date> {
        return try {
            val refreshTimeString = supabase.from("user_profile").select(Columns.list("last_email_refresh_time")) {
                filter {
                    eq("user_id", userID)
                }
            }.decodeSingle<Map<String, String>>()?.get("last_email_refresh_time")
            val refreshTime = LocalDateTime.parse(refreshTimeString)
            val date = java.util.Date.from(refreshTime.atZone(ZoneId.systemDefault()).toInstant())
            Result.success(date)
        } catch (e: Exception) {
            println(e)
            Result.failure(Exception("Failed to get refresh time"))
        }
    }

    override suspend fun updateJobApplicationStatus(jobApplicationID: String, statusID: Int): Result<Boolean> {
        return try {
            if (statusID == 5) {
                supabase.from("job_application_detail").delete() {
                    filter {
                        eq("app_id", jobApplicationID)
                    }
                }
                Result.success(true)
            }
            else {
                supabase.from("job_application_detail").update(
                    mapOf("app_status_id" to statusID)
                ) {
                    filter {
                        eq("app_id", jobApplicationID)
                    }
                }
                Result.success(true)
            }
        } catch (e: Exception) {
            println(e)
            Result.failure(Exception("Failed to update job application status"))
        }
    }


    override suspend fun getJobWithStatus(userId: String, statusID: Int): Result<List<JobApplication>> {
        return try {
            val jobApplications = supabase.from("job_application_detail").select() {
                filter {
                    eq("user_id", userId)
                    eq("app_status_id", statusID)
                }
            }.decodeList<JobApplication>()
            Result.success(jobApplications)
        } catch (e: Exception) {
            println(e)
            Result.failure(Exception("Failed to get job applications with status"))
        }
    }

    override suspend fun getRecruiterEmail(recruiterEmailID: Int): Result<String> {
        return try {
            val recruiter = supabase.from("recruiter").select() {
                filter {
                    eq("recruiter_id", recruiterEmailID)
                }
            }.decodeList<Recruiter>()
            Result.success(recruiter[0].recruiterEmail)
        } catch (e: Exception) {
            println(e)
            Result.failure(Exception("Failed to get recruiter email"))
        }
    }

    override suspend fun getRecruiterID(recruiterEmail: String): Result<Int> {
        return try {
            var idList = supabase.from("recruiter").select {
                filter {
                    eq("email", recruiterEmail)
                }
            }.decodeList<Recruiter>()
            if (idList.isNotEmpty()) {
                Result.success(idList[0].recruiterID!!)
            } else {
                Result.failure(Exception("Recruiter not found"))
            }
        } catch (e: Exception) {
            println(e)
            Result.failure(Exception("Failed to get recruiter ID"))
        }
    }


    override suspend fun getProgress(userId: String): Result<IJobApplicationRepository.Progress> {

        // applied
        val appliedJobs = getJobWithStatus(userId, 1)
        val appliedCount = appliedJobs.getOrNull()?.size ?: 0
        var appliedProgress = mutableListOf<IJobApplicationRepository.JobProgress>()
        for (job in appliedJobs.getOrNull() ?: listOf()) {
            val item = IJobApplicationRepository.JobProgress(job.jobTitle, job.companyName, getRecruiterEmail(job.recruiterEmailID).getOrNull()!!)
            appliedProgress.add(item)
        }

        // interviewed
        val interviewedJobs = getJobWithStatus(userId, 2)
        val interviewedCount = interviewedJobs.getOrNull()?.size ?: 0
        val interviewedProgress = mutableListOf<IJobApplicationRepository.JobProgress>()
        for (job in interviewedJobs.getOrNull() ?: listOf()) {
            val item = IJobApplicationRepository.JobProgress(job.jobTitle, job.companyName, getRecruiterEmail(job.recruiterEmailID).getOrNull()!!)
            interviewedProgress.add(item)
        }

        // offer
        val offerJobs = getJobWithStatus(userId, 3)
        val offerCount = offerJobs.getOrNull()?.size ?: 0
        val offerProgress = mutableListOf<IJobApplicationRepository.JobProgress>() // Change to mutableListOf
        for (job in offerJobs.getOrNull() ?: listOf()) {
            val item = IJobApplicationRepository.JobProgress(job.jobTitle, job.companyName, getRecruiterEmail(job.recruiterEmailID).getOrNull()!!)
            offerProgress.add(item)
        }

        // other
        val otherJobs = getJobWithStatus(userId, 4)
        val otherCount = otherJobs.getOrNull()?.size ?: 0
        val otherProgress = mutableListOf<IJobApplicationRepository.JobProgress>() // Change to mutableListOf
        for (job in otherJobs.getOrNull() ?: listOf()) {
            val item = IJobApplicationRepository.JobProgress(job.jobTitle, job.companyName, getRecruiterEmail(job.recruiterEmailID).getOrNull()!!)
            otherProgress.add(item)
        }


        return Result.success(IJobApplicationRepository.Progress(
            appliedItemCount = appliedCount,
            interviewedItemCount = interviewedCount,
            offerItemCount = offerCount,
            otherItemCount = otherCount,
            appliedJobs = appliedProgress,
            interviewedJobs = interviewedProgress,
            offerJobs = offerProgress,
            otherJobs = otherProgress
        ))
    }


    override suspend fun getAppliedJobs(userId: String): Result<List<Pair<IJobApplicationRepository.JobProgress, String>>> {
        val AllJobs = supabase.from("job_application_detail").select() {
            filter {
                eq("user_id", userId)
            }
        }.decodeList<JobApplication>()
        var result = mutableListOf<Pair<IJobApplicationRepository.JobProgress, String>>()
        for (job in AllJobs) {
            val item = IJobApplicationRepository.JobProgress(job.jobTitle, job.companyName, getRecruiterEmail(job.recruiterEmailID).getOrNull()!!)
            result.add(Pair(item, job.appID!!))
        }
        return Result.success(result)
    }





    override suspend fun getCorrespondJobs(
        userId: String,
        email: String
    ): Result<List<Pair<IJobApplicationRepository.JobProgress, String>>> {
        val AllJobs = supabase.from("job_application_detail").select() {
            filter {
                eq("user_id", userId)
                eq("recruiter_email_id", getRecruiterID(email).getOrNull()!!)
            }
        }.decodeList<JobApplication>()
        var result = mutableListOf<Pair<IJobApplicationRepository.JobProgress, String>>()
        for (job in AllJobs) {
            val item = IJobApplicationRepository.JobProgress(job.jobTitle, job.companyName, getRecruiterEmail(job.recruiterEmailID).getOrNull()!!)
            result.add(Pair(item, job.appID!!))
        }
        return Result.success(result)
    }


    override suspend fun getAllRecruiterEmailAddress(userId: String): List<String> {
        // find all jobs
        val jobs = supabase.from("job_application_detail").select() {
            filter {
                eq("user_id", userId)
            }
        }.decodeList<JobApplication>()
        var emailIDs = mutableListOf<Int>()
        var result = mutableListOf<String>()
        for (job in jobs) {
            val recruiterID = job.recruiterEmailID
            val recruiter = supabase.from("recruiter").select() {
                filter {
                    eq("recruiter_id", recruiterID)
                }
            }.decodeList<Recruiter>()
            result.add(recruiter[0].recruiterEmail)
        }
        return result

    }

    override suspend fun initializeRefreshTime(userId: String): Result<Boolean> {
        return try {
            val jobApplications = supabase.from("job_application_detail").select() {
                filter {
                    eq("user_id", userId)
                }
            }.decodeList<JobApplication>()

            if (jobApplications.isEmpty()) {
                updateRefreshTime(userId)
            }
            Result.success(true)
        } catch (e: Exception) {
            println(e)
            Result.failure(Exception("Failed to initialize refresh time"))
        }
    }

    override suspend fun getGmailPassword(userId: String): Result<String?> {
        return try {
            var password = supabase.from("user_profile").select(Columns.list("gmail_app_password")) {
                filter {
                    eq("user_id", userId)
                }
            }.decodeSingle<Map<String, String?>>().get("gmail_app_password")
            Result.success(password)
        } catch (e: Exception) {
            println(e)
            Result.failure(Exception("Failed to get gmail password"))
        }
    }

    override suspend fun getGmailAccount(userId: String): Result<String?> {
        return try {
            var account = supabase.from("user_profile").select(Columns.list("linked_gmail_account")) {
                filter {
                    eq("user_id", userId)
                }
            }.decodeSingle<Map<String, String?>>().get("linked_gmail_account")
            Result.success(account)
        } catch (e: Exception) {
            println(e)
            Result.failure(Exception("Failed to get gmail account"))
        }
    }
}

