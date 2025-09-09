package ca.uwaterloo.persistence

import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import model.UserProfile
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns


class AuthRepository(private val supabase: SupabaseClient) : IAuthRepository{

    //register a new user with email and password and return user id
    //password needs to be at least 6 chars
    //if the email has already used, log in
    //have to check data is inserted successfully in user_profile (need to implemented later)
    override suspend fun signUpUser(
        email: String,
        password: String,
        firstname: String,
        lastname: String
    ): Result<String> {
        return try {
            // Register user with built-in email provider
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            // Get the user info from current session
            val user = supabase.auth.retrieveUserForCurrentSession(updateSession = true)

            // Insert firstname and lastname into the user_profile table
            val userProfile = UserProfile(
                userId = user.id,
                firstName = firstname,
                lastName = lastname,
                email = email
            )
            supabase.from("user_profile").insert(userProfile)

            // Return user ID if successful
            Result.success(user.id)
        } catch (e: Exception) {
            // Return specific error message as failure
            val errorMessage = if (e.message?.contains("User already registered") == true) {
                "User already registered. Please sign in."
            } else {
                "Error during sign-up: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    //sign in the user with email and password
    override suspend fun signInUser(email: String, password: String): Result<String> {
        return try {
            val result = supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = supabase.auth.currentUserOrNull()
            if (user != null) {
                Result.success(user.id) // Sign-in successful, return user ID

            } else {
                Result.failure(Exception("Invalid credentials or session not created."))
            }
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("Invalid login credentials") == true -> "Sign in failed. Please use the correct email and password."
                else -> "Sign-in failed: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    //sign out the user in the current session
    override suspend fun signOutUser(): String {
        return try {
            // Sign out the current user
            supabase.auth.signOut()
            "User signed out successfully."
        } catch (e: Exception) {
            "Error during sign-out: ${e.message}"
        }
    }

    // Send OTP to the user's email for passwordless login
    // Sign in with email and OTP
// Send OTP
    override suspend fun sendOtpToEmail(email: String): Result<Boolean> {
        return try {
            // Check if the email exists in the user_profile table
            val existingUser = supabase.from("user_profile")
                .select {
                    filter {
                        eq("email", email)
                    }
                }
                .decodeSingleOrNull<UserProfile>()

            if (existingUser == null) {
                // If the email does not exist in user_profile, return an error
                return Result.failure(Exception("This email hasn't been registered, please sign up."))
            }
            supabase.auth.signInWith(OTP) {
                this.email = email
            }
            Result.success(true) // OTP successfully sent
        } catch (e: Exception) {
            Result.failure(Exception("Failed to send OTP: ${e.message}"))
        }
    }

    // Verify OTP
    override suspend fun verifyEmailOtp(email: String, token: String): Result<String> {
        return try {
            // Verify the token
            supabase.auth.verifyEmailOtp(
                email = email,
                token = token,
                type = OtpType.Email.EMAIL
            )

            // Retrieve the user session after successful OTP verification
            val user = supabase.auth.currentUserOrNull()
                ?: return Result.failure(Exception("Session not created after OTP verification."))

            Result.success(user.id) // Return user ID
        } catch (e: Exception) {
            Result.failure(Exception("Failed to verify OTP: ${e.message}"))
        }
    }


}
