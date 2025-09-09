package ca.uwaterloo.controller

// import ca.uwaterloo.persistence.DBStorage

import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.PersonalProject
import ca.uwaterloo.model.WorkExperience
import ca.uwaterloo.persistence.IUserProfileRepository
import ca.uwaterloo.service.EmailValidatingService
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate

class ProfileController(private val userProfileRepository: IUserProfileRepository) {

    // verify user's linked email and password
    suspend fun verifyUserLinkedEmailAndPassword(email: String, password: String): Boolean {
        val emailValidating = EmailValidatingService()
        return emailValidating.verifyEmail(email, password)
    }

    /**********************************************************
     *                                                        *
     *   The following 4 functions are for linking Gmail      *
     *   account and sending Gmail frontend                   *
     *   Call getters when sending emails                     *
     *   Call setters when linking Gmail account              *
     *                                                        *
     **********************************************************/

    // get user's linked gmail account
    suspend fun getUserLinkedGmailAccount(userId: String): Result<String> {
        return userProfileRepository.getUserLinkedGmailAccount(userId)
    }

    // edit user's linked gmail account
    // must prompt user to only enter gmail account (end with @gmail.com)
    suspend fun editUserLinkedGmailAccount(userId: String, linkedGmailAccount: String): Result<Boolean> {
        return userProfileRepository.editUserLinkedGmailAccount(userId, linkedGmailAccount)
    }

    // get user's gmail app password
    suspend fun getUserGmailAppPassword(userId: String): Result<String> {
        return userProfileRepository.getUserGmailAppPassword(userId)
    }

    // edit user's gmail app password
    suspend fun editUserGmailAppPassword(userId: String, gmailAppPassword: String): Result<Boolean> {
        return userProfileRepository.editUserGmailAppPassword(userId, gmailAppPassword)
    }


    // get user's name and display it on profile page
    suspend fun getUserName(userId: String): Result<String> {
        return userProfileRepository.getUserName(userId)
    }

    // get user's email and display it on profile page
    suspend fun getUserEmail(userId: String): Result<String> {
        return userProfileRepository.getUserEmail(userId)
    }

    // get user's city
    suspend fun getUserCity(userId: String): Result<String> {
        return userProfileRepository.getUserCity(userId)
    }

    // get user's phone
    suspend fun getUserPhone(userId: String): Result<String> {
        return userProfileRepository.getUserPhone(userId)
    }

    // update user's city and phone
    suspend fun updateCityPhone(userId: String, cityName: String?, phone: String?): Result<Boolean> {
        return userProfileRepository.updateCityPhone(userId, cityName, phone)
    }

    // get user's skills
    suspend fun getSkills(userId: String): Result<List<String>> {
        return userProfileRepository.getSkills(userId)
    }

    // add skill to db
    suspend fun addSkill(userId: String, skill: String): Result<Boolean> {
        return userProfileRepository.addSkill(userId, skill)
    }

    // delete skill from db
    suspend fun deleteSkill(userId: String, skill: String): Result<Boolean> {
        return userProfileRepository.deleteSkill(userId, skill)
    }

    // get user's linkedin url
    suspend fun getUserLinkedIn(userId: String): Result<String> {
        return userProfileRepository.getUserLinkedIn(userId)
    }

    // get user's github url
    suspend fun getUserGithub(userId: String): Result<String> {
        return userProfileRepository.getUserGithub(userId)
    }

    // get user's personal website url
    suspend fun getUserPersonalWebsite(userId: String): Result<String> {
        return userProfileRepository.getUserPersonalWebsite(userId)
    }

    // update user's linkedin, github, and personal website urls
    suspend fun updateUserLinks(
        userId: String,
        linkedinUrl: String?,
        githubUrl: String?,
        personalWebsiteUrl: String?
    ): Result<Boolean> {
        return userProfileRepository.updateUserLinks(userId, linkedinUrl, githubUrl, personalWebsiteUrl)
    }

    //
    //education exp
    //

    // get education exp by user id
    suspend fun getEducation(userId: String): Result<List<EducationWithDegreeName>> {
        return userProfileRepository.getEducation(userId)
    }

    // add education record to db
    suspend fun addEducation(
        userId: String,
        degreeId: Int,
        major: String,
        gpa: Float?,
        startDate: LocalDate,
        endDate: LocalDate,
        institutionName: String
    ): Result<Boolean> {
        return userProfileRepository.addEducation(
            userId = userId,
            degreeId = degreeId,
            major = major,
            gpa = gpa,
            startDate = startDate,
            endDate = endDate,
            institutionName = institutionName
        )
    }

    //update education record in db
    suspend fun updateEducation(
        userId: String,
        educationId: String,
        degreeId: Int,
        major: String,
        gpa: Float?,
        startDate: LocalDate,
        endDate: LocalDate,
        institutionName: String
    ): Result<Boolean> {
        return userProfileRepository.updateEducation(
            userId = userId,
            educationID = educationId,
            degreeId = degreeId,
            major = major,
            gpa = gpa,
            startDate = startDate,
            endDate = endDate,
            institutionName = institutionName
        )
    }

    // delete education record from db
    suspend fun deleteEducation(educationID: String): Result<Boolean> {
        return userProfileRepository.deleteEducation(educationID)
    }



    //
    //working exp
    //

    // get work exp by user id
    suspend fun getWorkExperience(userId: String): Result<List<WorkExperience>> {
        return userProfileRepository.getWorkExperience(userId)
    }

    // add work exp record to db
    suspend fun addWorkExperience(
        userId: String,
        companyName: String,
        currentlyWorking: Boolean,
        title: String,
        startDate: LocalDate,
        endDate: LocalDate,
        description: String?
    ): Result<Boolean> {
        return userProfileRepository.addWorkExperience(
            userId = userId,
            companyName = companyName,
            currentlyWorking = currentlyWorking,
            title = title,
            startDate = startDate,
            endDate = endDate,
            description = description
        )
    }

    //update work exp record in db
    suspend fun updateWorkExperience(
        userId: String,
        workExperienceID: String,
        companyName: String,
        currentlyWorking: Boolean,
        title: String,
        startDate: LocalDate,
        endDate: LocalDate,
        description: String?
    ): Result<Boolean> {
        return userProfileRepository.updateWorkExperience(
            userId = userId,
            workExperienceID = workExperienceID,
            companyName = companyName,
            currentlyWorking = currentlyWorking,
            title = title,
            startDate = startDate,
            endDate = endDate,
            description = description
        )
    }

    // delete work exp record from db
    suspend fun deleteWorkExperience(workExperienceID: String): Result<Boolean> {
        return userProfileRepository.deleteWorkExperience(workExperienceID)
    }

    // get degree name by id
    suspend fun getDegreeNameById(degreeId: Int): Result<String> {
        return userProfileRepository.getDegreeNameById(degreeId)
    }

    // get degree id by name
    suspend fun getDegreeIdByName(degreeName: String): Result<Int> {
        return userProfileRepository.getDegreeIdByName(degreeName)
    }

    // get projects by userid
    suspend fun getProjects(userId: String): Result<List<PersonalProject>> {
        return userProfileRepository.getProjects(userId)
    }

    // add project to db
    suspend fun addProject(userId: String, projectName: String, description: String?): Result<Boolean> {
        return userProfileRepository.addProject(userId, projectName, description)
    }

    // update project in db
    suspend fun updateProject(
        userId: String,
        projectID: String,
        projectName: String,
        description: String?
    ): Result<Boolean> {
        return userProfileRepository.updateProject(userId, projectID, projectName, description)
    }

    // delete project from db
    suspend fun deleteProject(projectID: String): Result<Boolean> {
        return userProfileRepository.deleteProject(projectID)
    }
}
