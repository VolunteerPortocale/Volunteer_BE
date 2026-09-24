package com.portocale.volunteer.event

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(name = "Event")
data class EventApi(
    val id: String,
    val details: EventDetailsApi,
    val category: EventCategoryApi,
    val storageFolderId: String,
    val status: EventStatusApi,
    val createdAt: Instant? = null,
    val createdBy: String,
    val lastModifiedAt: Instant? = null,
    val lastModifiedBy: String,
)

@Schema(name = "CreteEvent")
data class CreateEventApi(
    val details: CreateEventDetailsApi,
    val status: EventStatusApi,
    val category: EventCategoryApi,
    val storageFolderId: String? = null,
    val createdAt: Instant? = Instant.now(),
    val createdBy: String,
)

@Schema(name = "EventDetails")
data class EventDetailsApi(
    val title: String,
    val description: String,
    val startTime: Instant,
    val endTime: Instant?,
    val location: String?,
    val nrVolunteers: Int,
    val contactPhone: String?,
    val contactEmail: String?,
    val dressCode: EventDressCodeApi
)
@Schema(name = "CreateEventDetails")
data class CreateEventDetailsApi(
    val title: EventTitleApi,
    val description: EventDescriptionApi,
    val startTime: Instant,
    val endTime: Instant?,
    val location: String? = null,
    val nrVolunteers: Int,
    val contactPhone: String? = null,
    val contactEmail: String? = null,
    val dressCode: EventDressCodeApi
)

@Schema(name = "EventTitle")
data class EventTitleApi(
    val ro: String,
    val en: String?,
    val ru: String?
)

@Schema(name = "EventDescription")
data class EventDescriptionApi(
    val ro: String,
    val en: String?,
    val ru: String?
)

@Schema(name = "EventStatus", enumAsRef = true)
enum class EventStatusApi {
    DRAFT,
    PENDING_APPROVAL,
    PUBLISHED,
    APPLICATIONS_CLOSED,
    FULL,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    POSTPONED,
    REJECTED
}

@Schema(name = "EventCategory", enumAsRef = true)
enum class EventCategoryApi {
    SOCIAL,
    ANIMAL_CARE,
    CHILDREN_AND_YOUTH,
    EDUCATION,
    ENVIRONMENT,
    HEALTH,
    DISABILITY_SUPPORT,
    ELDERLY_CARE,
    DISASTER,
    POVERTY,
    CULTURE,
    SPORT,
    FESTIVALS,
    TECHNOLOGY,
    BUSINESS,
    EMPLOYMENT,
    SCIENCE,
    AGRICULTURE,
    CONSTRUCTION,
    RELIGION,
    HUMAN_RIGHTS,
    LEGAL,
    SAFETY,
    FAMILY,
    LGBTQ_PLUS,
    REFUGEE_SUPPORT,
    INTERNATIONAL_VOLUNTEERING,
    TOURISM,
    HERITAGE,
    MEDIA,
    GARDENING,
    PEACE,
    ADDICTION_RECOVERY,
    OTHER
}

@Schema(name = "EventStatusHistory")
data class EventStatusHistoryApi(
    val status: EventStatusApi,
    val occurredAt: Instant,
    val modifiedBy: String
)

@Schema(name = "EventFile")
data class EventFileApi(
    val id: String,
    val eventId: String,
    val type: EventFileTypeApi,
    val storageFileId: String,
    val originalName: String,
    val contentType: String,
    val size: Long,
    val index: Int,
    val createdAt: Instant
)

@Schema(name = "EventFileType", enumAsRef = true)
enum class EventFileTypeApi {
    COVER,
    GALLERY,
    ATTACHMENT
}

@Schema(name = "EventDressCode", enumAsRef = true )
enum class EventDressCodeApi {
    CASUAL,
    FORMAL,
    COSTUME
}
