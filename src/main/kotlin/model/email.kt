package model

data class email (
    var senderEmail: String,
    var password: String,
    var recipient: String,
    var subject: String,
    var text: String,
    var fileURLs: List<String>,
    var fileNames: List<String>
    )