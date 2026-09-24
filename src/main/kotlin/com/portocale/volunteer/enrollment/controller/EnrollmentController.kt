package com.portocale.volunteer.enrollment.controller

import com.portocale.volunteer.config.toLanguageApi
import com.portocale.volunteer.enrollment.service.EnrollmentService
import com.portocale.volunteer.enrollment.toCreateEnrollmentApi
import com.portocale.volunteer.enrollment.toEnrollmentGql
import com.portocale.volunteer.event.Event
import com.portocale.volunteer.event.EventCategory
import com.portocale.volunteer.event.EventDescription
import com.portocale.volunteer.event.EventDetails
import com.portocale.volunteer.event.EventStatus
import com.portocale.volunteer.event.EventTitle
import com.portocale.volunteer.event.repository.EventRepository
import com.portocale.volunteer.graphql.model.CreateEnrollmentInputGQL
import com.portocale.volunteer.graphql.model.EnrollmentGQL
import java.time.Instant
import java.util.Locale
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.web.bind.annotation.PostMapping
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

    @MutationMapping
    fun enroll(
        @Argument input: CreateEnrollmentInputGQL,
        locale: Locale
    ): EnrollmentGQL {
        return enrollmentService.enroll(
            request = input.toCreateEnrollmentApi(),
            language = locale.toLanguageApi()
        ).toEnrollmentGql()
    }
}