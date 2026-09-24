package com.portocale.volunteer.event.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.event.CreateEventApi
import com.portocale.volunteer.event.EventApi

interface EventService {
    fun getById(id: String, language: LanguageApi): EventApi
    fun create(event: CreateEventApi, language: LanguageApi): EventApi
    fun getAll(language: LanguageApi): List<EventApi>
}
