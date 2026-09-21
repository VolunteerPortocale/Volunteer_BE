package com.portocale.volunteer.event.controller

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.event.Event
import com.portocale.volunteer.event.EventCategory
import com.portocale.volunteer.event.EventDescription
import com.portocale.volunteer.event.EventDetails
import com.portocale.volunteer.event.EventNotFoundException
import com.portocale.volunteer.event.EventStatus
import com.portocale.volunteer.event.EventTitle
import com.portocale.volunteer.event.repository.EventRepository
import com.portocale.volunteer.event.service.EventService
import com.portocale.volunteer.event.toEventApi
import com.portocale.volunteer.event.toEventGQL
import com.portocale.volunteer.graphql.model.EventGQL
import com.portocale.volunteer.graphql.model.ModifyEventInputGQL
import com.portocale.volunteer.graphql.model.PublishEventInputGQL
import java.time.Instant
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
        return eventRepository.findAll().map { it.toEventApi().toEventGQL() }
    }

    @QueryMapping
    fun getEventById(@Argument id: String): EventGQL {
        return eventService.getById(id, LanguageApi.RO).toEventGQL()
    }

    @MutationMapping
    fun publishEvent(@Argument input: PublishEventInputGQL): EventGQL {
        val event = input.toNewEntity()
        return eventRepository.save(event).toEventApi().toEventGQL()
    }

    @MutationMapping
    fun modifyEventById(@Argument id: String, @Argument input: ModifyEventInputGQL): EventGQL {
        val existing = eventRepository.findById(id)
            .orElseThrow { EventNotFoundException("Event not found: $id") }
        val updated = existing.applyModifications(input)
        return eventRepository.save(updated).toEventApi().toEventGQL()
    }

    private fun PublishEventInputGQL.toNewEntity(): Event {
        val startInstant = runCatching { Instant.parse(startDate) }.getOrDefault(Instant.now())
        val endInstant = endDate?.let { runCatching { Instant.parse(it) }.getOrNull() }

        return Event(
            details = EventDetails(
                title = EventTitle(ro = name, en = name, ru = name),
                description = EventDescription(ro = description, en = description, ru = description),
                startTime = startInstant,
                endTime = endInstant
            ),
            category = eventType?.name?.let { runCatching { EventCategory.valueOf(it) }.getOrNull() }
                ?: EventCategory.OTHER,
            status = EventStatus.PUBLISHED,
            startTime = startInstant,
            createdBy = "system",
            lastModifiedBy = "system",
            location = location,
            nrVolunteers = nrVolunteers,
            startDate = startDate,
            endDate = endDate,
            dates = dates.orEmpty(),
            time = time,
            coverImage = coverImage,
            images = images.orEmpty(),
            eventType = eventType?.name,
            dressCode = dressCode?.name,
            duration = duration?.name,
            contactEmail = email,
            contactPhone = phone
        )
    }

    private fun Event.applyModifications(input: ModifyEventInputGQL): Event {
        return applyDetailsModifications(input)
            .applyScheduleModifications(input)
            .applyContactAndMediaModifications(input)
    }

    private fun Event.applyDetailsModifications(input: ModifyEventInputGQL): Event {
        val updatedTitle = input.name?.let { details.title.copy(ro = it, en = it, ru = it) } ?: details.title
        val updatedDesc = input.description?.let { details.description.copy(ro = it, en = it, ru = it) }
            ?: details.description
        val updatedStart = input.startDate?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: startTime
        val updatedEnd = input.endDate?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: details.endTime

        return copy(
            details = details.copy(
                title = updatedTitle,
                description = updatedDesc,
                startTime = updatedStart,
                endTime = updatedEnd
            ),
            startTime = updatedStart,
            location = input.location ?: location,
            nrVolunteers = input.nrVolunteers ?: nrVolunteers
        )
    }

    private fun Event.applyScheduleModifications(input: ModifyEventInputGQL): Event {
        return copy(
            startDate = input.startDate ?: startDate,
            endDate = input.endDate ?: endDate,
            dates = input.dates ?: dates,
            time = input.time ?: time,
            duration = input.duration?.name ?: duration
        )
    }

    private fun Event.applyContactAndMediaModifications(input: ModifyEventInputGQL): Event {
        val updatedCover = if (input.removeCover == true) null else (input.coverImage ?: coverImage)
        return copy(
            coverImage = updatedCover,
            images = input.images ?: images,
            dressCode = input.dressCode?.name ?: dressCode,
            eventType = input.eventType?.name ?: eventType,
            contactEmail = input.email ?: contactEmail,
            contactPhone = input.phone ?: contactPhone,
            lastModifiedAt = Instant.now(),
            lastModifiedBy = "system"
        )
    }
}

