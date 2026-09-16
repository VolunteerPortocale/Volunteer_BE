package com.portocale.volunteer.event.controller

import com.portocale.volunteer.config.toLanguageApi
import com.portocale.volunteer.event.EventFileApi
import com.portocale.volunteer.event.EventFileType
import com.portocale.volunteer.event.service.EventFileService
import com.portocale.volunteer.event.toEventFileResponseApi
import java.util.*
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
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
}
