package com.portocale.volunteer.event.controller

import com.portocale.volunteer.config.toLanguageApi
import com.portocale.volunteer.event.service.EventService
import com.portocale.volunteer.event.toCreateEventApi
import com.portocale.volunteer.event.toEventGql
import com.portocale.volunteer.graphql.model.CreateEventInputGQL
import com.portocale.volunteer.graphql.model.EventGQL
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.security.Principal
import java.util.Locale

@Controller
class EventGraphQLController(
    private val eventService: EventService
) {
    @QueryMapping
    fun getAllEvents(locale: Locale): List<EventGQL> {
        return eventService.getAll(locale.toLanguageApi()).map { it.toEventGql() }
    }

    @QueryMapping
    fun getEventById(@Argument id: String, locale: Locale): EventGQL {
        return eventService.getById(id, locale.toLanguageApi()).toEventGql()
    }

    @MutationMapping
    fun createEvent(
        @Argument input: CreateEventInputGQL,
        principal: Principal,
        locale: Locale
    ): EventGQL {
        return eventService.create(
            input.toCreateEventApi(createdBy = principal.name),
            locale.toLanguageApi()
        ).toEventGql()
    }
}










