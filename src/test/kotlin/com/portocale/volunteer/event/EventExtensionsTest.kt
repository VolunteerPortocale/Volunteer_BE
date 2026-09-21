package com.portocale.volunteer.event

import com.portocale.volunteer.graphql.model.DressCodeGQL
import com.portocale.volunteer.graphql.model.EventDurationGQL
import com.portocale.volunteer.graphql.model.EventTypeGQL
import java.time.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class EventExtensionsTest {

    @Test
    fun `toEventGQL maps all fields from EventApi`() {
        val eventApi = EventApi(
            id = "evt-42",
            details = EventDetailsApi(
                title = "Charity Gala",
                description = "Annual fundraising gala dinner",
                startTime = Instant.parse("2026-12-15T19:00:00Z"),
                endTime = Instant.parse("2026-12-15T23:00:00Z")
            ),
            category = EventCategoryApi.CULTURE,
            storageFolderId = "fld-42",
            status = EventStatusApi.PUBLISHED,
            startTime = Instant.parse("2026-12-15T19:00:00Z"),
            createdBy = "admin",
            lastModifiedBy = "admin",
            location = "Grand Hotel Ballroom",
            nrVolunteers = 25,
            startDate = "2026-12-15T19:00:00Z",
            endDate = "2026-12-15T23:00:00Z",
            dates = listOf("2026-12-15"),
            time = "19:00 - 23:00",
            coverImage = "https://example.com/gala.jpg",
            images = listOf("https://example.com/gala1.jpg", "https://example.com/gala2.jpg"),
            eventType = "CULTURE",
            dressCode = "FORMAL",
            duration = "ONE_DAY",
            contactPhone = "+37360000000",
            contactEmail = "gala@charity.org"
        )

        val gql = eventApi.toEventGQL()

        assertNotNull(gql)
        assertEquals("evt-42", gql.id)
        assertEquals("Charity Gala", gql.name)
        assertEquals("Annual fundraising gala dinner", gql.description)
        assertEquals("Grand Hotel Ballroom", gql.location)
        assertEquals(25, gql.nrVolunteers)
        assertEquals("2026-12-15T19:00:00Z", gql.startDate)
        assertEquals("2026-12-15T23:00:00Z", gql.endDate)
        assertEquals(listOf("2026-12-15"), gql.dates)
        assertEquals("19:00 - 23:00", gql.time)
        assertEquals("https://example.com/gala.jpg", gql.coverImage)
        assertEquals(2, gql.images.size)
        assertEquals(EventTypeGQL.CULTURE, gql.eventType)
        assertEquals(DressCodeGQL.FORMAL, gql.dressCode)
        assertEquals(EventDurationGQL.ONE_DAY, gql.duration)
        assertEquals("+37360000000", gql.phone)
        assertEquals("gala@charity.org", gql.email)
    }

    @Test
    fun `toEventGQL handles null and missing optional fields with safe defaults`() {
        val eventApi = EventApi(
            id = "evt-minimal",
            details = EventDetailsApi(
                title = "Minimal Event",
                description = "Minimal Description",
                startTime = Instant.parse("2026-10-10T10:00:00Z"),
                endTime = null
            ),
            category = EventCategoryApi.OTHER,
            storageFolderId = "fld-min",
            status = EventStatusApi.DRAFT,
            startTime = Instant.parse("2026-10-10T10:00:00Z"),
            createdBy = "user",
            lastModifiedBy = "user"
        )

        val gql = eventApi.toEventGQL()

        assertEquals("evt-minimal", gql.id)
        assertEquals("Minimal Event", gql.name)
        assertEquals("Minimal Description", gql.description)
        assertEquals("", gql.location)
        assertEquals(0, gql.nrVolunteers)
        assertNull(gql.coverImage)
        assertEquals(emptyList<String>(), gql.images)
        assertEquals(EventTypeGQL.OTHER, gql.eventType)
        assertEquals(DressCodeGQL.CASUAL, gql.dressCode)
        assertEquals(EventDurationGQL.ONE_DAY, gql.duration)
    }
}
