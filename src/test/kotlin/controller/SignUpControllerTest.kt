package controller

import ca.uwaterloo.controller.SignUpController
import ca.uwaterloo.persistence.IAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SignUpControllerTest {

    private lateinit var authRepository: IAuthRepository
    private lateinit var signUpController: SignUpController

    @BeforeEach
    fun setUp() {
        authRepository = mock()
        signUpController = SignUpController(authRepository)
    }

    @Test
    fun `signUpUser returns success with valid credentials`() = runTest {
        val email = "wrw@1.com"
        val password = "123456"
        val firstname = "Cherry"
        val lastname = "Wang"
        whenever(authRepository.signUpUser(email, password, firstname, lastname))
            .thenReturn(Result.success("c9498eec-ac17-4a3f-8d91-61efba3f7277"))
        val result = signUpController.signUpUser(email, password, firstname, lastname)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `signUpUser returns failure with existing email`() = runTest {
        val email = "wrw040613@gmail.com"
        val password = "111111"
        val firstname = "C"
        val lastname = "W"

        whenever(authRepository.signUpUser(email, password, firstname, lastname))
            .thenReturn(Result.failure(Exception("User already registered")))
        val result = signUpController.signUpUser(email, password, firstname, lastname)
        assertTrue(result.isFailure)
        assertEquals("User already registered", result.exceptionOrNull()?.message)
    }
}
