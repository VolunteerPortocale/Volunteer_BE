package com.portocale.volunteer.event.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.event.EventFile
import com.portocale.volunteer.event.EventFileType
import org.springframework.web.multipart.MultipartFile

interface EventFileService {
    fun getById(id: String): EventFile
    fun upload(
        eventId: String,
        type: EventFileType,
        index: Int,
        file: MultipartFile,
        language: LanguageApi
    ): EventFile
    fun uploadBatch(
        eventId: String,
        type: EventFileType,
        files: List<MultipartFile>,
        language: LanguageApi
    ): List<EventFile>
    fun getAllByEventId(eventId: String): List<EventFile>
    fun delete(eventId: String, fileId: String)
    fun deleteBatch(eventId: String, fileIds: List<String>)
}
