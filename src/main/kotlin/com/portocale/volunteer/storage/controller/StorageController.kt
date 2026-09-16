package com.portocale.volunteer.storage.controller

import com.portocale.volunteer.event.service.EventFileService
import com.portocale.volunteer.storage.service.StorageService
import java.nio.charset.StandardCharsets
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody

@RestController
@RequestMapping("/api/v1/files")
class StorageController(
    private val eventFileService: EventFileService,
    private val storageService: StorageService
) {

    @GetMapping("/{id}/content")
    fun download(
        @PathVariable id: String
    ): ResponseEntity<StreamingResponseBody> {

        val file = eventFileService.getById(id)

        val body = StreamingResponseBody { outputStream ->
            storageService.download(
                fileId = file.storageFileId,
                outputStream = outputStream
            )
        }

        return ResponseEntity.ok()
            .contentType(
                MediaType.parseMediaType(file.contentType)
            )
            .contentLength(file.size)
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.inline()
                    .filename(
                        file.originalName,
                        StandardCharsets.UTF_8
                    )
                    .build()
                    .toString()
            )
            .body(body)
    }

    @PostMapping(
        "/test-upload",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun testUpload(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Any> {

        val rootFolderId = storageService.getOrCreateRootFolderId()

        val uploadedFile = storageService.upload(
            file = file,
            folderId = rootFolderId
        )

        return ResponseEntity.ok(uploadedFile)
    }
}
