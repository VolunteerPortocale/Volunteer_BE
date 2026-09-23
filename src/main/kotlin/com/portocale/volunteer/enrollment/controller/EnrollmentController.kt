package com.portocale.volunteer.enrollment.controller

import com.portocale.volunteer.config.toLanguageApi
import com.portocale.volunteer.enrollment.CreateEnrollmentApi
import com.portocale.volunteer.enrollment.EnrollmentResponseApi
import com.portocale.volunteer.enrollment.service.EnrollmentService
import com.portocale.volunteer.event.Event
import com.portocale.volunteer.event.EventCategory
import com.portocale.volunteer.event.EventDescription
import com.portocale.volunteer.event.EventDetails
import com.portocale.volunteer.event.EventStatus
import com.portocale.volunteer.event.EventTitle
import com.portocale.volunteer.event.repository.EventRepository
import jakarta.validation.Valid
import java.time.Instant
import java.util.Locale
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/enrollment")
class EnrollmentController(
    private val enrollmentService: EnrollmentService,
    private val eventRepository: EventRepository
) {

    //TO BE DELETED WHEN WE HAVE A PROPER WAY TO CREATE EVENTS
    @PostMapping("/test-event")
    fun createTestEvent(): String {
        val event = eventRepository.save(
            Event(
                details = EventDetails(
                    title = EventTitle("Test"),
                    description = EventDescription("Test"),
                    startTime = Instant.now()
                ),
                category = EventCategory.OTHER,
                status = EventStatus.PUBLISHED,
                startTime = Instant.now(),
                createdBy = "admin",
                lastModifiedBy = "admin"
            )
        )
        return event.id!!
    }
    //////////////////////////////////////////////////////////////

    @PostMapping
    fun enroll(
        @Valid @RequestBody request: CreateEnrollmentApi,
        locale: Locale
    ): EnrollmentResponseApi {
        return enrollmentService.enroll(
            request = request,
            language = locale.toLanguageApi()
        )
    }
}