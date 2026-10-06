package com.portocale.volunteer.event.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.event.EventConflictException
import com.portocale.volunteer.event.EventFile
import com.portocale.volunteer.event.EventFileType
import com.portocale.volunteer.event.EventNotFoundException
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

    @SuppressWarnings("TooGenericExceptionCaught")
    override fun uploadBatch(
        eventId: String,
        type: EventFileType,
        files: List<MultipartFile>,
        language: LanguageApi
    ): List<EventFile> {
        if (files.isEmpty()) return emptyList()

        val event = eventService.getById(eventId, language)
        validateFileLimit(eventId, type, files.size)

        val folderId = event.storageFolderId
            ?: throw StorageNotFoundException("Storage folder not found for event: $eventId")

        val existingFiles = eventFileRepository.findAllByEventId(eventId).filter { it.type == type }
        val startIndex = if (existingFiles.isEmpty()) 0 else (existingFiles.maxOf { it.index } + 1)

        val uploadedDriveIds = mutableListOf<String>()
        val savedFiles = mutableListOf<EventFile>()

        return try {
            files.forEachIndexed { i, file ->
                val uploaded = storageService.upload(file = file, folderId = folderId)
                uploadedDriveIds.add(uploaded.fileId)

                val saved = eventFileRepository.save(
                    EventFile(
                        eventId = eventId,
                        type = type,
                        storageFileId = uploaded.fileId,
                        originalName = uploaded.name,
                        contentType = uploaded.contentType,
                        size = uploaded.size,
                        index = startIndex + i
                    )
                )
                savedFiles.add(saved)
            }
            savedFiles
        } catch (exception: Exception) {
            uploadedDriveIds.forEach { fileId ->
                runCatching { storageService.delete(fileId) }
            }
            savedFiles.forEach { saved ->
                runCatching { eventFileRepository.delete(saved) }
            }
            throw exception
        }
    }

    override fun delete(eventId: String, fileId: String) {
        val file = eventFileRepository.findByIdAndEventId(fileId, eventId)
            ?: throw EventNotFoundException("File not found: $fileId for event: $eventId")

        runCatching {
            storageService.delete(file.storageFileId)
        }
        eventFileRepository.delete(file)
    }

    private fun validateFileLimit(
        eventId: String,
        type: EventFileType,
        incomingCount: Int = 1
    ) {
        val maxAllowed = when (type) {
            EventFileType.COVER -> MAX_COVER_COUNT
            EventFileType.GALLERY -> MAX_GALLERY_COUNT
            EventFileType.ATTACHMENT -> MAX_ATTACHMENT_COUNT
        }

        val currentCount = eventFileRepository.countByEventIdAndType(eventId, type)
        if (currentCount + incomingCount > maxAllowed) {
            throw EventConflictException(
                "Maximum limit of $maxAllowed file(s) reached for type $type"
            )
        }

    }
    override fun deleteBatch(eventId: String, fileIds: List<String>) {
        if (fileIds.isEmpty()) return
        val files = eventFileRepository.findAllById(fileIds)
            .filter { it.eventId == eventId }
        files.forEach { file ->
            runCatching {
                storageService.delete(file.storageFileId)
            }
        }
        eventFileRepository.deleteAll(files)
    }
}
