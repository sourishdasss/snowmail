package persistence

import ca.uwaterloo.persistence.AuthRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import kotlin.test.*

class AuthRepositoryTest {

    private lateinit var authRepository: AuthRepository

    @BeforeEach
    fun setUp() {
        println("Setup is running...")
        try {
            val supabaseClient = createSupabaseClient(
                supabaseUrl = "https://gwnlngyvkxdpodenpyyj.supabase.co",
                supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd3bmxuZ3l2a3hkcG9kZW5weXlqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjc5MTAxNTEsImV4cCI6MjA0MzQ4NjE1MX0.olncAUMxSOjcr0YjssWXThtXDXC3q4zasdNYdwavt8g"
            ) {
                install(Postgrest)
                install(Auth)
                install(Storage)
            }

            authRepository = AuthRepository(supabaseClient)
        } catch (e: Exception) {
            throw RuntimeException("Failed to initialize Supabase client: ${e.message}", e)
        }
    }


//test passed, and account already exists in the db, so commented out

//    @Test
//    fun `test user sign-up`(): Unit = runBlocking {
//        // Replace with test email and password
//        val email = "testuser@example.com"
//        val password = "password123"
//        val firstname = "Test"
//        val lastname = "User"
//
//        val result = authRepository.signUpUser(email, password, firstname, lastname)
//
//        assertTrue(result.isSuccess, "Sign-up should be successful")
//        val userId = result.getOrNull()
//        assertNotNull(userId, "User ID should not be null after successful sign-up")
//    }

    @Test
    fun `test user sign-in`(): Unit = runBlocking {
        // Use existing test credentials
        val email = "testuser@example.com"
        val password = "password123"

        val result = authRepository.signInUser(email, password)

        assertTrue(result.isSuccess, "Sign-in should be successful")
        val userId = result.getOrNull()
        assertNotNull(userId, "User ID should not be null after successful sign-in")
    }

    @Test
    fun `test user sign-out`() = runBlocking {
        // Ensure the user is signed in first
        val email = "testuser@example.com"
        val password = "password123"
        authRepository.signInUser(email, password)

        val result = authRepository.signOutUser()

        assertEquals("User signed out successfully.", result, "Sign-out message should be correct")
    }

    @Test
    fun `test sign-up with existing email`() = runBlocking {
        // Use an email that already exists
        val email = "testuser@example.com"
        val password = "password123"
        val firstname = "Test"
        val lastname = "User"

        val result = authRepository.signUpUser(email, password, firstname, lastname)

        assertTrue(result.isFailure, "Sign-up should fail for already registered user")
        val errorMessage = result.exceptionOrNull()?.message
        assertTrue(
            errorMessage?.contains("User already registered") == true,
            "Error message should indicate user already registered"
        )
    }

    @AfterEach
    fun cleanup(): Unit = runBlocking {


    }


}
