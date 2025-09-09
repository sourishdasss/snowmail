package ca.uwaterloo.controller
import ca.uwaterloo.persistence.IAuthRepository

class SignInController(private val authRepository: IAuthRepository) {

    //Sign in the user and return either the user id or error message
    suspend fun signInUser(email: String, password: String): Result<String> {
        return authRepository.signInUser(email, password)
    }

    //Sign out the user
    suspend fun logoutUser(): String {
        return authRepository.signOutUser()
    }

    //Send OTP to the user's email1
    suspend fun sendOtpToEmail(email: String): Result<Boolean> {
        return authRepository.sendOtpToEmail(email)
    }

    //Sign in the user with email and OTP
    suspend fun signInUserWithOTP(email: String, otp: String): Result<String> {
        return authRepository.verifyEmailOtp(email, otp)
    }

}