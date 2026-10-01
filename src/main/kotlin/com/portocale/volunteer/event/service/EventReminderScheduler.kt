package com.portocale.volunteer.event.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class EventReminderScheduler(
    private val eventService: EventService
) {
    @Scheduled(cron = "0 0 * * * *")
    fun sendUpcomingEventReminders() {
        eventService.sendUpcomingEventReminders()
    }
}
