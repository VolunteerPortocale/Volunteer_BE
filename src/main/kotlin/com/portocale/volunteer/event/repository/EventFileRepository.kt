package com.portocale.volunteer.event.repository

import com.portocale.volunteer.event.EventFile
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EventFileRepository : MongoRepository<EventFile, String> {
    fun findAllByEventId(eventId: String): List<EventFile>
    fun findByIdAndEventId(id: String, eventId: String): EventFile?
}
