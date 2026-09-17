package com.portocale.volunteer.event.repository

import com.portocale.volunteer.event.Event
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : MongoRepository<Event, String>
