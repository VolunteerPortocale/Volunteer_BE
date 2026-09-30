package com.portocale.volunteer.event.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.enrollment.Enrollment
import com.portocale.volunteer.enrollment.repository.EnrollmentRepository
import com.portocale.volunteer.event.Event
import com.portocale.volunteer.event.repository.EventRepository
import com.portocale.volunteer.notification.service.EmailService
import com.portocale.volunteer.users.repository.UserRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.slf4j.LoggerFactory
import org.springframework.mail.MailException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class EventReminderScheduler(
    private val eventRepository: EventRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val userRepository: UserRepository,
    private val emailService: EmailService
) {
    private val log = LoggerFactory.getLogger(EventReminderScheduler::class.java)

    companion object {
        private val FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.of("Europe/Chisinau"))
    }

    @Scheduled(cron = "0 0 * * * *")
    fun sendUpcomingEventReminders() {
        val windowStart = Instant.now().plus(24, ChronoUnit.HOURS)
        val windowEnd = windowStart.plus(1, ChronoUnit.HOURS)

        val eventsStartingTomorrow = eventRepository.findByStartTimeBetween(windowStart, windowEnd)

        for (event in eventsStartingTomorrow) {
            val eventId = event.id ?: continue
            val enrollments = enrollmentRepository.findByEventId(eventId)

            for (enrollment in enrollments) {
                notifyVolunteer(event, enrollment)
            }
        }
    }

    private fun notifyVolunteer(event: Event, enrollment: Enrollment) {
        val enrollmentId = enrollment.id ?: return
        val user = userRepository.findById(enrollment.userId).orElse(null) ?: return

        try {
            emailService.sendEventReminder(
                email = user.email,
                eventTitle = event.details.title.translated(LanguageApi.RO),
                eventLocation = event.details.location ?: "N/A",
                eventStartTime = FORMATTER.format(event.details.startTime),
                enrollmentId = enrollmentId,
                language = LanguageApi.RO
            )
            log.info("Sent 24h reminder to {} for event {}", user.email, event.id)
        } catch (e: MailException) {
            log.error("Failed to send reminder to {}: {}", user.email, e.message)
        }
    }
}
