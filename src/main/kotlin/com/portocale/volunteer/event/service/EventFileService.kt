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
}
