package com.portocale.volunteer.event.controller

import com.portocale.volunteer.config.toLanguageApi
import com.portocale.volunteer.event.EventFileApi
import com.portocale.volunteer.event.EventFileType
import com.portocale.volunteer.event.service.EventFileService
import com.portocale.volunteer.event.toEventFileResponseApi
import java.util.Locale
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/events")
class EventFileController(
    private val eventFileService: EventFileService
) {

    @PostMapping(
        "/{eventId}/files",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun upload(
        @PathVariable eventId: String,
        @RequestParam type: EventFileType,
        @RequestParam index: Int,
        @RequestPart file: MultipartFile,
        locale: Locale
    ): EventFileApi {

        return eventFileService
            .upload(
                eventId = eventId,
                type = type,
                index = index,
                file = file,
                locale.toLanguageApi()
            )
            .toEventFileResponseApi()
    }

    @PostMapping(
        "/{eventId}/files/batch",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadBatch(
        @PathVariable eventId: String,
        @RequestParam type: EventFileType,
        @RequestPart files: List<MultipartFile>,
        locale: Locale
    ): List<EventFileApi> {

        return eventFileService
            .uploadBatch(
                eventId = eventId,
                type = type,
                files = files,
                language = locale.toLanguageApi()
            )
            .map { it.toEventFileResponseApi() }
    }

    @DeleteMapping("/{eventId}/files/{fileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable eventId: String,
        @PathVariable fileId: String
    ) {
        eventFileService.delete(eventId = eventId, fileId = fileId)
    }

    @DeleteMapping("/{eventId}/files/batch")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBatch(
        @PathVariable eventId: String,
        @RequestParam fileIds: List<String>
    ) {
        eventFileService.deleteBatch(eventId = eventId, fileIds = fileIds)
    }
}
