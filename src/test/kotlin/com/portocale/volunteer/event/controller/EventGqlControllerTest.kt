package com.portocale.volunteer.event.controller

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.event.Event
import com.portocale.volunteer.event.EventApi
import com.portocale.volunteer.event.EventCategory
import com.portocale.volunteer.event.EventCategoryApi
import com.portocale.volunteer.event.EventDescription
import com.portocale.volunteer.event.EventDetails
import com.portocale.volunteer.event.EventDetailsApi
import com.portocale.volunteer.event.EventNotFoundException
import com.portocale.volunteer.event.EventStatus
import com.portocale.volunteer.event.EventStatusApi
import com.portocale.volunteer.event.EventTitle
import com.portocale.volunteer.event.repository.EventRepository
import com.portocale.volunteer.event.service.EventService
import com.portocale.volunteer.graphql.model.DressCodeGQL
import com.portocale.volunteer.graphql.model.EventDurationGQL
import com.portocale.volunteer.graphql.model.EventTypeGQL
import com.portocale.volunteer.graphql.model.ModifyEventInputGQL
import com.portocale.volunteer.graphql.model.PublishEventInputGQL
import java.time.Instant
import java.util.Optional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class EventGqlControllerTest {

    private val eventService: EventService = mock(EventService::class.java)
    private val eventRepository: EventRepository = mock(EventRepository::class.java)
    private val controller = EventGqlController(eventService, eventRepository)

    private fun sampleEventEntity(id: String = "event-1"): Event {
        return Event(
            id = id,
            details = EventDetails(
                title = EventTitle(ro = "Tree Planting Day", en = "Tree Planting Day", ru = "Tree Planting Day"),
                description = EventDescription(
                    ro = "Planting trees in the central park",
                    en = "Planting trees in the central park",
                    ru = "Planting trees in the central park"
                ),
                startTime = Instant.parse("2026-10-01T09:00:00Z"),
                endTime = Instant.parse("2026-10-01T17:00:00Z")
            ),
            category = EventCategory.ENVIRONMENT,
            storageFolderId = "folder-123",
            status = EventStatus.PUBLISHED,
            startTime = Instant.parse("2026-10-01T09:00:00Z"),
            createdBy = "organizer-1",
            lastModifiedBy = "organizer-1",
            location = "Central Park",
            nrVolunteers = 50,
            startDate = "2026-10-01T09:00:00Z",
            endDate = "2026-10-01T17:00:00Z",
            dates = listOf("2026-10-01"),
            time = "09:00 - 17:00",
            coverImage = "https://example.com/cover.jpg",
            images = listOf("https://example.com/image1.jpg"),
            dressCode = "CASUAL",
            duration = "ONE_DAY",
            contactPhone = "+1234567890",
            contactEmail = "trees@volunteer.org"
        )
    }

    private fun sampleEventApi(id: String = "event-1"): EventApi {
        return EventApi(
            id = id,
            details = EventDetailsApi(
                title = "Tree Planting Day",
                description = "Planting trees in the central park",
                startTime = Instant.parse("2026-10-01T09:00:00Z"),
                endTime = Instant.parse("2026-10-01T17:00:00Z")
            ),
            category = EventCategoryApi.ENVIRONMENT,
            storageFolderId = "folder-123",
            status = EventStatusApi.PUBLISHED,
            startTime = Instant.parse("2026-10-01T09:00:00Z"),
            createdBy = "organizer-1",
            lastModifiedBy = "organizer-1",
            location = "Central Park",
            nrVolunteers = 50,
            startDate = "2026-10-01T09:00:00Z",
            endDate = "2026-10-01T17:00:00Z",
            dates = listOf("2026-10-01"),
            time = "09:00 - 17:00",
            coverImage = "https://example.com/cover.jpg",
            images = listOf("https://example.com/image1.jpg"),
            dressCode = "CASUAL",
            duration = "ONE_DAY",
            contactPhone = "+1234567890",
            contactEmail = "trees@volunteer.org"
        )
    }

    @Test
    fun `getEventById returns EventGQL`() {
        val event = sampleEventApi("event-1")
        `when`(eventService.getById("event-1", LanguageApi.RO)).thenReturn(event)

        val result = controller.getEventById("event-1")

        assertNotNull(result)
        assertEquals("event-1", result.id)
        assertEquals("Tree Planting Day", result.name)
        assertEquals("Planting trees in the central park", result.description)
        assertEquals(50, result.nrVolunteers)
        assertEquals("Central Park", result.location)
    }

    @Test
    fun `getAllEvents returns list of EventGQL`() {
        val events = listOf(sampleEventEntity("event-1"), sampleEventEntity("event-2"))
        `when`(eventRepository.findAll()).thenReturn(events)

        val result = controller.getAllEvents()

        assertEquals(2, result.size)
        assertEquals("event-1", result[0].id)
        assertEquals("event-2", result[1].id)
    }

    @Test
    fun `publishEvent saves and returns EventGQL`() {
        val input = PublishEventInputGQL.builder()
            .setName("Beach Clean Up")
            .setDescription("Cleaning up the beach")
            .setLocation("Sunny Beach")
            .setNrVolunteers(20)
            .setStartDate("2026-11-01T10:00:00Z")
            .setEndDate("2026-11-01T15:00:00Z")
            .setDates(listOf("2026-11-01"))
            .setTime("10:00 - 15:00")
            .setCoverImage("https://example.com/beach.jpg")
            .setImages(listOf("https://example.com/img1.jpg"))
            .setEventType(EventTypeGQL.ENVIRONMENT)
            .setDressCode(DressCodeGQL.CASUAL)
            .setDuration(EventDurationGQL.ONE_DAY)
            .setEmail("clean@beach.org")
            .setPhone("+1234567890")
            .build()

        `when`(eventRepository.save(any(Event::class.java))).thenAnswer { invocation ->
            val toSave = invocation.getArgument<Event>(0)
            toSave.copy(id = "generated-id")
        }

        val result = controller.publishEvent(input)

        assertNotNull(result)
        assertEquals("generated-id", result.id)
        assertEquals("Beach Clean Up", result.name)
        assertEquals("Cleaning up the beach", result.description)
        assertEquals("Sunny Beach", result.location)
        assertEquals(20, result.nrVolunteers)
        assertEquals("clean@beach.org", result.email)
        assertEquals("+1234567890", result.phone)
        assertEquals(EventTypeGQL.ENVIRONMENT, result.eventType)
    }

    @Test
    fun `modifyEventById updates and returns EventGQL`() {
        val existing = sampleEventEntity("event-1")
        `when`(eventRepository.findById("event-1")).thenReturn(Optional.of(existing))
        `when`(eventRepository.save(any(Event::class.java))).thenAnswer { invocation ->
            invocation.getArgument<Event>(0)
        }

        val input = ModifyEventInputGQL.builder()
            .setName("Updated Tree Planting Day")
            .setNrVolunteers(100)
            .setLocation("Updated Park")
            .build()

        val result = controller.modifyEventById("event-1", input)

        assertNotNull(result)
        assertEquals("event-1", result.id)
        assertEquals("Updated Tree Planting Day", result.name)
        assertEquals(100, result.nrVolunteers)
        assertEquals("Updated Park", result.location)
    }

    @Test
    fun `modifyEventById removes coverImage when removeCover is true`() {
        val existing = sampleEventEntity("event-1")
        `when`(eventRepository.findById("event-1")).thenReturn(Optional.of(existing))
        `when`(eventRepository.save(any(Event::class.java))).thenAnswer { invocation ->
            invocation.getArgument<Event>(0)
        }

        val input = ModifyEventInputGQL.builder()
            .setRemoveCover(true)
            .build()

        val result = controller.modifyEventById("event-1", input)

        assertNotNull(result)
        assertNull(result.coverImage)
    }

    @Test
    fun `modifyEventById throws EventNotFoundException when event not found`() {
        `when`(eventRepository.findById("not-found")).thenReturn(Optional.empty())

        val input = ModifyEventInputGQL.builder()
            .setName("New Name")
            .build()

        assertThrows(EventNotFoundException::class.java) {
            controller.modifyEventById("not-found", input)
        }
    }
}
