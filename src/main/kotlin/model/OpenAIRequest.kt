package model

import co.touchlab.kermit.Message
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<Map<String, String>>,
    val max_tokens: Int
)