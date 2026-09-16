package com.portocale.volunteer.event

import com.portocale.volunteer.config.LanguageApi
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("events")
data class Event(
    @Id
    val id: String? = null,
    val details: EventDetails,
    val category: EventCategory,
    val storageFolderId: String? = null,
    val status: EventStatus,
    val statusHistory: List<EventStatusHistory>? = null,
    val startTime: Instant,
    val createdAt: Instant? = Instant.now(),
    val createdBy: String,
    val lastModifiedAt: Instant? = Instant.now(),
    val lastModifiedBy: String
)

@Document("eventFiles")
data class EventFile(
    @Id
    val id: String? = null,
    val eventId: String,
    val type: EventFileType,
    val storageFileId: String,
    val originalName: String,
    val contentType: String,
    val size: Long,
    val index: Int,
    val createdAt: Instant = Instant.now()
)

data class EventDetails(
    val title: EventTitle,
    val description: EventDescription,
    val startTime: Instant,
    val endTime: Instant? = null
)

enum class EventFileType {
    COVER,
    GALLERY,
    ATTACHMENT
}

data class EventTitle(
    val ro: String,
    val en: String? = null,
    val ru: String? = null
) {
    fun translated(language: LanguageApi): String {
        return when (language) {
            LanguageApi.RO -> ro
            LanguageApi.EN -> en ?: ro
            LanguageApi.RU -> ru ?: ro
        }
    }
}

data class EventDescription(
    val ro: String,
    val en: String? = null,
    val ru: String? = null
) {
    fun translated(language: LanguageApi): String {
        return when (language) {
            LanguageApi.RO -> ro
            LanguageApi.EN -> en ?: ro
            LanguageApi.RU -> ru ?: ro
        }
    }
}

data class EventStatusHistory(
    val status: EventStatus,
    val occurredAt: Instant? = Instant.now(),
    val modifiedBy: String
)

enum class EventStatus {
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

enum class EventCategory {
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
