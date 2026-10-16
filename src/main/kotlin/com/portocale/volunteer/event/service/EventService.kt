package com.portocale.volunteer.event.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.event.CreateEventApi
import com.portocale.volunteer.event.EventApi
import com.portocale.volunteer.event.EventFilterApi
import com.portocale.volunteer.event.PagedEventDataApi
import com.portocale.volunteer.event.UpdateEventApi
import org.springframework.security.access.prepost.PreAuthorize

interface EventService {
    fun getById(id: String, language: LanguageApi): EventApi
    fun getAll(language: LanguageApi): List<EventApi>
    fun getLastThreeActiveEvents(language: LanguageApi): List<EventApi>
    fun getCurrentUserEvents(userId: String, language: LanguageApi): List<EventApi>

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'NGO', 'VOLUNTEER')")
    fun getFilteredEvents(input: EventFilterApi, language: LanguageApi): PagedEventDataApi

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'NGO')")
    fun create(event: CreateEventApi, language: LanguageApi): EventApi
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'NGO')")
    fun update(id: String, event: UpdateEventApi, language: LanguageApi, userId: String): EventApi
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'NGO')")
    fun getEventsCreatedBy(userId: String, language: LanguageApi): List<EventApi>

    fun sendUpcomingEventReminders()
}

