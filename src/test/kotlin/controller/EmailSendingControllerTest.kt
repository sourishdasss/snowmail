
import ca.uwaterloo.persistence.DocumentRepository
import controller.SendEmailController
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import persistence.JobApplicationRepository


class EmailSendingControllerTest {


    @Test
    fun testWithoutAttachments() {
        assertDoesNotThrow {
            runBlocking {
                val senderEmail = "cs346test@gmail.com"
                val password = "qirk dyef rvbv bkka"
                val recipient = "irishuang1105@gmail.com"
                val subject = "subject"
                val text = "text"
                val userID = "ed52b6c4-2ae8-4b58-bacd-adc00082a505"
                val jobTitle = "Software Developer"
                val companyName = "Google"
                val supabase = createSupabaseClient(
                    supabaseUrl = "https://gwnlngyvkxdpodenpyyj.supabase.co",
                    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd3bmxuZ3l2a3hkcG9kZW5weXlqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjc5MTAxNTEsImV4cCI6MjA0MzQ4NjE1MX0.olncAUMxSOjcr0YjssWXThtXDXC3q4zasdNYdwavt8g"
                ) {
                    install(Postgrest)
                    install(Auth)
                    install(Storage)
                }
                val JobApplicationRepository = JobApplicationRepository(supabase)
                val documentRepository = DocumentRepository(supabase)
                val c = SendEmailController(JobApplicationRepository, documentRepository)
                c.send_email(recipient, subject, text, listOf(), listOf(), listOf(), userID, jobTitle, companyName)
            }

        }

    }


}