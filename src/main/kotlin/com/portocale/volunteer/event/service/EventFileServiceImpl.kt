package com.portocale.volunteer.event.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.event.EventConflictException
import com.portocale.volunteer.event.EventFile
import com.portocale.volunteer.event.EventFileType
import com.portocale.volunteer.event.repository.EventFileRepository
import com.portocale.volunteer.storage.StorageNotFoundException
import com.portocale.volunteer.storage.service.StorageService
import java.io.FileNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class EventFileServiceImpl(
    private val eventFileRepository: EventFileRepository,
    private val storageService: StorageService,
    private val eventService: EventService
) : EventFileService {

    companion object {
        private const val MAX_COVER_COUNT = 1
        private const val MAX_GALLERY_COUNT = 3
        private const val MAX_ATTACHMENT_COUNT = 2
    }

    override fun getById(id: String): EventFile {
        return eventFileRepository.findById(id)
            .orElseThrow {
                FileNotFoundException("File not found: $id")
            }
    }

    override fun getAllByEventId(eventId: String): List<EventFile> {
        return eventFileRepository.findAllByEventId(eventId)
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    override fun upload(
        eventId: String,
        type: EventFileType,
        index: Int,
        file: MultipartFile,
        language: LanguageApi
    ): EventFile {

        val event = eventService.getById(eventId, language)
        validateFileLimit(eventId, type)

        val folderId = event.storageFolderId
            ?: throw StorageNotFoundException("Storage folder not found for event: $eventId")

        val uploaded = storageService.upload(file = file, folderId = folderId)

        return try {
            eventFileRepository.save(
                EventFile(
                    eventId = eventId,
                    type = type,
                    storageFileId = uploaded.fileId,
                    originalName = uploaded.name,
                    contentType = uploaded.contentType,
                    size = uploaded.size,
                    index = index
                )
            )
        } catch (exception: Exception) {
            runCatching {
                storageService.delete(uploaded.fileId)
            }
            throw exception
        }
    }

    private fun validateFileLimit(eventId: String, type: EventFileType) {
        val maxAllowed = when (type) {
            EventFileType.COVER -> MAX_COVER_COUNT
            EventFileType.GALLERY -> MAX_GALLERY_COUNT
            EventFileType.ATTACHMENT -> MAX_ATTACHMENT_COUNT
        }

        val currentCount = eventFileRepository.countByEventIdAndType(eventId, type)
        if (currentCount >= maxAllowed) {
            throw EventConflictException(
                "Maximum limit of $maxAllowed file(s) reached for type $type"
            )
        }
    }
}
