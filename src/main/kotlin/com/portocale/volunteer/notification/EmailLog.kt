package com.portocale.volunteer.notification

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("emailLogs")
data class EmailLog(
    @Id
    val id: String? = null,
    val recipient: String,
    val subject: String,
    val content: String,
    val templateName: String? = null,
    val status: EmailStatus,
    val errorMessage: String? = null,
    val sentAt: Instant = Instant.now()
)

enum class EmailStatus {
    SENT,
    FAILED
}
