package com.portocale.volunteer.event.repository

import com.portocale.volunteer.event.Event
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import org.springframework.data.mongodb.repository.Query

@Repository
interface EventRepository : MongoRepository<Event, String>{
    @Query("{ 'details.startTime': { '\$gte': ?0, '\$lt': ?1 } }")
    fun findByStartTimeBetween(from: Instant, to: Instant): List<Event>
}
