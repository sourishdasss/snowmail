package ca.uwaterloo.service

import java.util.*
import javax.mail.AuthenticationFailedException
import javax.mail.Session

class EmailValidatingService {

    fun verifyEmail(email: String, password: String): Boolean {
        // Configure SMTP server properties
        val properties = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", 587)
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")

        }

        val session: Session = Session.getInstance(properties)

        return try {
            // Try connecting to the SMTP server
            val transport = session.getTransport("smtp")
            transport.connect("smtp.gmail.com", email, password) // Attempt to connect
            transport.close()   // Close the connection
            true // If no exception, credentials are valid
        } catch (e: AuthenticationFailedException) {
            println("Authentication failed: ${e.message}")
            false // Invalid credentials
        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            false // Other issues (e.g., network, server not reachable)
        }
    }
}




