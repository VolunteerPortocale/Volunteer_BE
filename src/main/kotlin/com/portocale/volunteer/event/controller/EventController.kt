package com.portocale.volunteer.event.controller

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.event.EventApi
import com.portocale.volunteer.event.service.EventService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Events", description = "Group of endpoints for event management")
@RequestMapping("/api/v1/events")
class EventController(
    private val eventService: EventService
) {

    @GetMapping
    @Operation(summary = "Get all events")
    fun getAll(): List<EventApi> {
        TODO("Not yet implemented")
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by Id")
    @ApiResponses(
        value = [ApiResponse(
            description = "OK",
            responseCode = "200",
            content = [Content(schema = Schema(implementation = EventApi::class))]
        )]
    )
    fun getById(@PathVariable id: String): EventApi {
        return eventService.getById(id, LanguageApi.RO)
    }
}
