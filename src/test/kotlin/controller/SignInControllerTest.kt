package controller

import ca.uwaterloo.controller.SignInController
import ca.uwaterloo.persistence.IAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SignInControllerTest {

    private lateinit var authRepository: IAuthRepository
    private lateinit var signInController: SignInController

    @BeforeEach
    fun setUp() {
        authRepository = mock()
        signInController = SignInController(authRepository)
    }

    @Test
    fun `signInUser returns success with valid credentials`() = runTest {
        whenever(authRepository.signInUser("wrw040613@gmail.com", "Wrw54321"))
            .thenReturn(Result.success("c9498eec-ac17-4a3f-8d91-61efba3f7277"))

        val result = signInController.signInUser("wrw040613@gmail.com", "Wrw54321")

        assertTrue(result.isSuccess)
        assertEquals("c9498eec-ac17-4a3f-8d91-61efba3f7277", result.getOrNull())
    }

    @Test
    fun `signInUser returns failure with invalid credentials`() = runTest {
        whenever(authRepository.signInUser("wrw040613@gmail.com", "wrw54321"))
            .thenReturn(Result.failure(Exception("Invalid credentials")))

        val result = signInController.signInUser("wrw040613@gmail.com", "wrw54321")

        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }
}

