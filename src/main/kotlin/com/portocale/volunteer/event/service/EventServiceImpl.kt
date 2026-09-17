package com.portocale.volunteer.event.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.config.StorageConfig
import com.portocale.volunteer.event.CreateEventApi
import com.portocale.volunteer.event.EventApi
import com.portocale.volunteer.event.EventNotFoundException
import com.portocale.volunteer.event.repository.EventRepository
import com.portocale.volunteer.event.toEntity
import com.portocale.volunteer.event.toEventApi
import com.portocale.volunteer.storage.service.StorageService
import org.springframework.stereotype.Service

@Service
class EventServiceImpl(
    private val eventRepository: EventRepository,
    private val storageService: StorageService,
    private val properties: StorageConfig
) : EventService {
    override fun getById(id: String, language: LanguageApi): EventApi {
        return eventRepository.findById(id)
            .orElseThrow { EventNotFoundException("Event not found: $id") }
            .toEventApi(language)
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    override fun create(event: CreateEventApi, language: LanguageApi): EventApi {
        val savedEvent = eventRepository.save(event.toEntity())

        return try {
            val rootFolderId = storageService.getOrCreateRootFolderId()

            val folderId = storageService.createFolder(
                name = savedEvent.id!!,
                parentFolderId = rootFolderId
            )

            eventRepository.save(
                savedEvent.copy(
                    storageFolderId = folderId
                )
            ).toEventApi(language)
        } catch (exception: Exception) {
            eventRepository.delete(savedEvent)
            throw exception
        }
    }
}
