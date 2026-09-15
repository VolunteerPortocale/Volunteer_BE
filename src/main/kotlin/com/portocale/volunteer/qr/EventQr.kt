package com.portocale.volunteer.qr
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

@Document(collection = "event_qr_codes")
@CompoundIndex(name = "event_user_unique_idx", def = "{'eventId': 1, 'userId': 1}", unique = true)
data class EventQr(
    @Id val id: String? = null,
    val eventId: String,
    val userId: String,
    val payload: String,
    val createdAt: Instant = Instant.now()
)

interface EventQrRepository : MongoRepository<EventQr, String> {
    fun existsByEventIdAndUserId(eventId: String, userId: String): Boolean
}
