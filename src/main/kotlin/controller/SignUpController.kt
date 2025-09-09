package ca.uwaterloo.controller

import ca.uwaterloo.persistence.IAuthRepository
import kotlinx.coroutines.runBlocking
import integration.SupabaseClient

class SignUpController(private val authRepository: IAuthRepository) {

    // Sign up a new user and return either userId or error message
    fun signUpUser(email: String, password: String, firstname: String, lastname: String): Result<String> {
        return runBlocking {
            authRepository.signUpUser(email, password, firstname, lastname)
        }
    }
}
