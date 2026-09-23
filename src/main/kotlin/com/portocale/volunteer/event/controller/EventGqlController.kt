package com.portocale.volunteer.event.controller

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.event.EventNotFoundException
import com.portocale.volunteer.event.applyModifications
import com.portocale.volunteer.event.repository.EventRepository
import com.portocale.volunteer.event.service.EventService
import com.portocale.volunteer.event.toEntity
import com.portocale.volunteer.event.toEventGQL
import com.portocale.volunteer.graphql.model.EventGQL
import com.portocale.volunteer.graphql.model.ModifyEventInputGQL
import com.portocale.volunteer.graphql.model.PublishEventInputGQL
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class EventGqlController(
    private val eventService: EventService,
    private val eventRepository: EventRepository
) {

    @QueryMapping
    fun getAllEvents(): List<EventGQL> {
        return eventRepository.findAll().map { it.toEventGQL() }
    }

    @QueryMapping
    fun getEventById(@Argument id: String): EventGQL {
        return eventService.getById(id, LanguageApi.RO).toEventGQL()
    }

    @MutationMapping
    fun publishEvent(@Argument input: PublishEventInputGQL): EventGQL {
        val event = input.toEntity()
        return eventRepository.save(event).toEventGQL()
    }

    @MutationMapping
    fun modifyEventById(@Argument id: String, @Argument input: ModifyEventInputGQL): EventGQL {
        val existing = eventRepository.findById(id)
            .orElseThrow { EventNotFoundException("Event not found: $id") }
        val updated = existing.applyModifications(input)
        return eventRepository.save(updated).toEventGQL()
    }
}
