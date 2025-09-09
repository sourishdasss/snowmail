package controller

import ca.uwaterloo.controller.ProgressController
import ca.uwaterloo.persistence.DocumentRepository
import ca.uwaterloo.persistence.IJobApplicationRepository
import integration.OpenAIClient
import integration.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import persistence.JobApplicationRepository

class ProgressControllerTest {

    private lateinit var jobApplicationRepository: IJobApplicationRepository
    private lateinit var openAIClient: OpenAIClient
    private lateinit var progressController: ProgressController
    private lateinit var supabaseClient: SupabaseClient
    private lateinit var documentRepository: DocumentRepository

    @BeforeEach
    fun setUp() {
        val supabase = createSupabaseClient(
            supabaseUrl = "https://gwnlngyvkxdpodenpyyj.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd3bmxuZ3l2a3hkcG9kZW5weXlqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjc5MTAxNTEsImV4cCI6MjA0MzQ4NjE1MX0.olncAUMxSOjcr0YjssWXThtXDXC3q4zasdNYdwavt8g"
        ) {
            install(Postgrest)
            install(Auth)
            install(Storage)
        }
        jobApplicationRepository = JobApplicationRepository(supabase)
        documentRepository = DocumentRepository(supabase)
        openAIClient = OpenAIClient(HttpClient(CIO))
        progressController = ProgressController(jobApplicationRepository, openAIClient)
    }

    @Test
     fun `test getProgress`() {
        assertDoesNotThrow {
            runBlocking {
                progressController.getProgress("ed52b6c4-2ae8-4b58-bacd-adc00082a505")
            }
        }
    }


    @Test
    fun `test modifyStatus`() {
        assertDoesNotThrow {
            runBlocking {
                progressController.modifyStatus("788db112-76bd-4cb1-95ff-93a0e1d7e078", 3)
            }
        }
    }


    @Test
    fun `test getNewEmails`() {
        assertDoesNotThrow {
            runBlocking {
                progressController.getNewEmails("ed52b6c4-2ae8-4b58-bacd-adc00082a505", "cs346test@gmail.com", "qirk dyef rvbv bkka", documentRepository)
            }
        }
    }







}