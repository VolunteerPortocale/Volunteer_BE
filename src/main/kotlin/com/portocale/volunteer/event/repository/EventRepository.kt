package com.portocale.volunteer.event.repository

import com.portocale.volunteer.event.Event
import com.portocale.volunteer.event.EventStatus
import java.time.Instant
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : MongoRepository<Event, String>, EventRepositoryCustom {
    @Query("{ 'details.startTime': { '\$gte': ?0, '\$lt': ?1 } }")
    fun findByStartTimeBetween(from: Instant, to: Instant): List<Event>
    fun findTop3ByStatusInOrderByCreatedAtDesc(status: List<EventStatus>): List<Event>
    fun findAllByCreatedBy(userId: String): List<Event>
}
