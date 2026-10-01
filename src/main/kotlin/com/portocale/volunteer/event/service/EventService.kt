package com.portocale.volunteer.event.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.event.CreateEventApi
import com.portocale.volunteer.event.EventApi
import com.portocale.volunteer.event.UpdateEventApi

interface EventService {
    fun getById(id: String, language: LanguageApi): EventApi
    fun create(event: CreateEventApi, language: LanguageApi): EventApi
    fun getAll(language: LanguageApi): List<EventApi>
    fun update(id: String, event: UpdateEventApi, language: LanguageApi, modifiedBy: String): EventApi
    fun sendUpcomingEventReminders()
}

