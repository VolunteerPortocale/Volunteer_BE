package com.portocale.volunteer.event.controller

import com.portocale.volunteer.config.jwt.Principal
import com.portocale.volunteer.config.toLanguageApi
import com.portocale.volunteer.event.service.EventFileService
import com.portocale.volunteer.event.service.EventService
import com.portocale.volunteer.event.toCreateEventApi
import com.portocale.volunteer.event.toEventFileGql
import com.portocale.volunteer.event.toEventFileResponseApi
import com.portocale.volunteer.event.toEventFilterApi
import com.portocale.volunteer.event.toEventGql
import com.portocale.volunteer.event.toPageEventDataGql
import com.portocale.volunteer.event.toUpdateEventApi
import com.portocale.volunteer.graphql.model.CreateEventInputGQL
import com.portocale.volunteer.graphql.model.EventFileGQL
import com.portocale.volunteer.graphql.model.EventFilterInputGQL
import com.portocale.volunteer.graphql.model.EventGQL
import com.portocale.volunteer.graphql.model.PagedEventDataGQL
import com.portocale.volunteer.graphql.model.UpdateEventInputGQL
import java.util.*
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class EventGraphQLController(
    private val eventService: EventService,
    private val eventFileService: EventFileService
) {
    @QueryMapping
    fun getAllEvents(locale: Locale): List<EventGQL> {
        return eventService.getAll(locale.toLanguageApi()).map { it.toEventGql() }
    }

    @QueryMapping
    fun getLastThreeActiveEvents(locale: Locale): List<EventGQL> {
        return eventService.getLastThreeActiveEvents(locale.toLanguageApi()).map { it.toEventGql() }
    }

    @QueryMapping
    fun getEventById(@Argument id: String, locale: Locale): EventGQL {
        return eventService.getById(id, locale.toLanguageApi()).toEventGql()
    }

    @QueryMapping
    fun getEventFiles(@Argument eventId: String): List<EventFileGQL> {
        return eventFileService.getAllByEventId(eventId)
            .map { it.toEventFileResponseApi().toEventFileGql() }
    }

    @SchemaMapping(typeName = "Event", field = "files")
    fun files(event: EventGQL): List<EventFileGQL> {
        return eventFileService.getAllByEventId(event.id)
            .map { it.toEventFileResponseApi().toEventFileGql() }
    }

    @QueryMapping
    fun getCurrentUserEvents(
        principal: Principal,
        locale: Locale
    ): List<EventGQL> {
        return eventService.getCurrentUserEvents(
            principal.userId,
            locale.toLanguageApi()
        ).map { it.toEventGql() }
    }

    @QueryMapping
    fun getEventsCreatedByCurrentUser(
        principal: Principal,
        locale: Locale
    ): List<EventGQL> {
        return eventService.getEventsCreatedBy(principal.userId, locale.toLanguageApi())
            .map { it.toEventGql() }
    }

    @MutationMapping
    fun createEvent(
        @Argument input: CreateEventInputGQL,
        principal: Principal,
        locale: Locale
    ): EventGQL {
        return eventService.create(
            input.toCreateEventApi(createdBy = principal.userId),
            locale.toLanguageApi()
        ).toEventGql()
    }

    @MutationMapping
    fun updateEvent(
        @Argument id: String,
        @Argument input: UpdateEventInputGQL,
        principal: Principal,
        locale: Locale
    ): EventGQL {
        return eventService.update(
            id = id,
            event = input.toUpdateEventApi(),
            language = locale.toLanguageApi(),
            userId = principal.userId
        ).toEventGql()
    }

    @QueryMapping
    fun getFilteredEvents(
        @Argument input: EventFilterInputGQL,
        locale: Locale
    ): PagedEventDataGQL {
        return eventService.getFilteredEvents(input.toEventFilterApi(), locale.toLanguageApi()).toPageEventDataGql()
    }

    @MutationMapping
    fun approveEvent(
        @Argument eventId: String
    ): Boolean {
        return eventService.approveEvent(eventId)
    }

    @MutationMapping
    fun rejectEvent(
        @Argument eventId: String
    ): Boolean {
        return eventService.rejectEvent(eventId)
    }

}










