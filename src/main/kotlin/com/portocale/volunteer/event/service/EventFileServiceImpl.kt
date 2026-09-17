package com.portocale.volunteer.event.service

import com.portocale.volunteer.config.LanguageApi
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

    override fun getById(id: String): EventFile {
        return eventFileRepository.findById(id)
            .orElseThrow {
                FileNotFoundException("File not found: $id")
            }
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

        val folderId = event.storageFolderId

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
}
