package com.portocale.volunteer.event.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.config.StorageConfig
import com.portocale.volunteer.enrollment.EnrollmentResponseApi
import com.portocale.volunteer.enrollment.service.EnrollmentService
import com.portocale.volunteer.event.CreateEventApi
import com.portocale.volunteer.event.Event
import com.portocale.volunteer.event.EventApi
import com.portocale.volunteer.event.EventNotFoundException
import com.portocale.volunteer.event.UpdateEventApi
import com.portocale.volunteer.event.repository.EventRepository
import com.portocale.volunteer.event.toEntity
import com.portocale.volunteer.event.toEventApi
import com.portocale.volunteer.event.toUpdatedEntity
import com.portocale.volunteer.notification.service.EmailService
import com.portocale.volunteer.storage.service.StorageService
import com.portocale.volunteer.users.repository.UserRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.mail.MailException
import org.springframework.stereotype.Service

@Service
class EventServiceImpl(
    private val eventRepository: EventRepository,
    @Lazy private val enrollmentService: EnrollmentService,
    private val userRepository: UserRepository,
    private val emailService: EmailService,
    private val storageService: StorageService,
    private val properties: StorageConfig
) : EventService {

    private val log = LoggerFactory.getLogger(EventServiceImpl::class.java)

    companion object {
        private val FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.of("Europe/Chisinau"))
    }

    override fun getAll(language: LanguageApi): List<EventApi> {
        return eventRepository.findAll()
            .map { it.toEventApi(language) }
    }

    override fun getById(id: String, language: LanguageApi): EventApi {
        return eventRepository.findById(id)
            .orElseThrow { EventNotFoundException("Event not found: $id") }
            .toEventApi(language)
    }

    override fun update(id: String, event: UpdateEventApi, language: LanguageApi, modifiedBy: String): EventApi {
        val existingEvent = eventRepository.findById(id)
            .orElseThrow { EventNotFoundException("Event not found: $id") }
        val updatedEvent = existingEvent.toUpdatedEntity(event, modifiedBy)
        return eventRepository.save(updatedEvent).toEventApi(language)
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    override fun create(event: CreateEventApi, language: LanguageApi): EventApi {
        val savedEvent = eventRepository.save(event.toEntity())

        return try {
            val rootFolderId = storageService.getOrCreateRootFolderId()

            val folderId = storageService.createFolder(
                name = savedEvent.id!!,
                parentFolderId = rootFolderId
            )

            eventRepository.save(
                savedEvent.copy(
                    storageFolderId = folderId
                )
            ).toEventApi(language)
        } catch (exception: Exception) {
            eventRepository.delete(savedEvent)
            throw exception
        }
    }

    override fun sendUpcomingEventReminders() {
        val windowStart = Instant.now().plus(24, ChronoUnit.HOURS)
        val windowEnd = windowStart.plus(1, ChronoUnit.HOURS)

        val eventsStartingTomorrow = eventRepository.findByStartTimeBetween(windowStart, windowEnd)

        for (event in eventsStartingTomorrow) {
            val eventId = event.id ?: continue
            val enrollments = enrollmentService.getByEventId(eventId)

            for (enrollment in enrollments) {
                notifyVolunteer(event, enrollment)
            }
        }
    }

    private fun notifyVolunteer(event: Event, enrollment: EnrollmentResponseApi) {
        val user = userRepository.findById(enrollment.userId).orElse(null) ?: return

        try {
            emailService.sendEventReminder(
                email = user.email,
                eventTitle = event.details.title.translated(LanguageApi.RO),
                eventLocation = event.details.location ?: "N/A",
                eventStartTime = FORMATTER.format(event.details.startTime),
                enrollmentId = enrollment.id,
                language = LanguageApi.RO
            )
            log.info("Sent 24h reminder to {} for event {}", user.email, event.id)
        } catch (e: MailException) {
            log.error("Failed to send reminder to {}: {}", user.email, e.message)
        }
    }
}
