package com.portocale.volunteer.enrollment.controller

import com.portocale.volunteer.config.toLanguageApi
import com.portocale.volunteer.enrollment.service.EnrollmentService
import com.portocale.volunteer.enrollment.toCreateEnrollmentApi
import com.portocale.volunteer.enrollment.toEnrollmentGql
import com.portocale.volunteer.event.CreateEventApi
import com.portocale.volunteer.event.CreateEventDetailsApi
import com.portocale.volunteer.event.EventCategoryApi
import com.portocale.volunteer.event.EventDescriptionApi
import com.portocale.volunteer.event.EventStatusApi
import com.portocale.volunteer.event.EventTitleApi
import com.portocale.volunteer.event.service.EventService
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
    private val eventService: EventService
) {

    //TO BE DELETED WHEN WE HAVE A PROPER WAY TO CREATE EVENTS
    @PostMapping("/test-event")
    fun createTestEvent(locale: Locale): String {
        val event = eventService.create(
            event = CreateEventApi(
                details = CreateEventDetailsApi(
                    title = EventTitleApi(ro = "Test", en = "Test", ru = null),
                    description = EventDescriptionApi(ro = "Test", en = "Test", ru = null),
                    startTime = Instant.now(),
                    endTime = null
                ),
                category = EventCategoryApi.OTHER,
                status = EventStatusApi.PUBLISHED,
                startTime = Instant.now(),
                createdBy = "admin",
                lastModifiedBy = "admin"
            ),
            language = locale.toLanguageApi()
        )
        return event.id
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
