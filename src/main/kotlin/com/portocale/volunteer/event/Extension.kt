@file:Suppress("TooManyFunctions")

package com.portocale.volunteer.event

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.graphql.model.DressCodeGQL
import com.portocale.volunteer.graphql.model.EventDurationGQL
import com.portocale.volunteer.graphql.model.EventGQL
import com.portocale.volunteer.graphql.model.EventTypeGQL
import com.portocale.volunteer.graphql.model.ModifyEventInputGQL
import com.portocale.volunteer.graphql.model.PublishEventInputGQL
import java.time.Instant


fun Event.toEventApi(language: LanguageApi): EventApi {
    return EventApi(
        id = id ?: error(IllegalStateException("ID is null")),
        details = details.toEventDetailsApi(language),
        category = category.toEventCategoryApi(),
        storageFolderId = storageFolderId ?: error(IllegalStateException("StorageFolderId is null")),
        status = status.toEventStatusApi(),
        startTime = startTime,
        createdAt = createdAt,
        createdBy = createdBy,
        lastModifiedAt = lastModifiedAt,
        lastModifiedBy = lastModifiedBy
    )
}

fun Event.toEventGQL(language: LanguageApi = LanguageApi.RO): EventGQL = EventGQL(
    id,
    details.title.translated(language),
    details.description.translated(language),
    details.location.orEmpty(),
    0,
    startTime.toString(),
    details.endTime?.toString(),
    details.dates ?: emptyList(),
    null,
    null,
    emptyList(),
    runCatching { EventTypeGQL.valueOf(category.name) }.getOrDefault(EventTypeGQL.OTHER),
    runCatching { DressCodeGQL.valueOf(details.dressCode.name) }.getOrDefault(DressCodeGQL.CASUAL),
    EventDurationGQL.ONE_DAY,
    details.contactEmail.orEmpty(),
    details.contactPhone.orEmpty()
)

fun EventApi.toEventGQL(): EventGQL = EventGQL(
    id,
    details.title,
    details.description,
    details.location.orEmpty(),
    0,
    startTime.toString(),
    details.endTime?.toString(),
    details.dates ?: emptyList(),
    null,
    null,
    emptyList(),
    runCatching { EventTypeGQL.valueOf(category.name) }.getOrDefault(EventTypeGQL.OTHER),
    runCatching { DressCodeGQL.valueOf(details.dressCode.name) }.getOrDefault(DressCodeGQL.CASUAL),
    EventDurationGQL.ONE_DAY,
    details.contactEmail.orEmpty(),
    details.contactPhone.orEmpty()
)

fun PublishEventInputGQL.toEntity(): Event {
    val startInstant = runCatching { Instant.parse(startDate) }.getOrDefault(Instant.now())
    val endInstant = endDate?.let { runCatching { Instant.parse(it) }.getOrNull() }
    return Event(
        details = EventDetails(
            title = EventTitle(ro = name, en = name, ru = name),
            description = EventDescription(ro = description, en = description, ru = description),
            startTime = startInstant,
            endTime = endInstant,
            dates = dates.orEmpty(),
            location = location,
            contactPhone = phone,
            contactEmail = email,
            dressCode = dressCode?.name?.let { runCatching { EventDressCode.valueOf(it) }.getOrNull() }
                ?: EventDressCode.CASUAL
        ),
        category = eventType?.name?.let { runCatching { EventCategory.valueOf(it) }.getOrNull() }
            ?: EventCategory.OTHER,
        status = EventStatus.PUBLISHED,
        startTime = startInstant,
        createdBy = "system",
        lastModifiedBy = "system"
    )
}

fun Event.applyModifications(input: ModifyEventInputGQL): Event {
    val updatedStart = input.startDate?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: details.startTime
    val updatedEnd = input.endDate?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: details.endTime
    val updatedDressCode = input.dressCode?.name?.let {
        runCatching { EventDressCode.valueOf(it) }.getOrNull()
    } ?: details.dressCode
    val updatedDetails = details.copy(
        title = input.name?.let { details.title.copy(ro = it, en = it, ru = it) } ?: details.title,
        description = input.description?.let { details.description.copy(ro = it, en = it, ru = it) } ?: details.description,
        startTime = updatedStart,
        endTime = updatedEnd,
        dates = input.dates ?: details.dates,
        location = input.location ?: details.location,
        contactPhone = input.phone ?: details.contactPhone,
        contactEmail = input.email ?: details.contactEmail,
        dressCode = updatedDressCode
    )
    val updatedCategory = input.eventType?.name?.let {
        runCatching { EventCategory.valueOf(it) }.getOrNull()
    } ?: category
    return copy(
        details = updatedDetails,
        category = updatedCategory,
        startTime = updatedStart,
        lastModifiedAt = Instant.now(),
        lastModifiedBy = "system"
    )
}


fun EventDetails.toEventDetailsApi(language: LanguageApi): EventDetailsApi {
    return EventDetailsApi(
        title = title.translated(language),
        description = description.translated(language),
        startTime = startTime,
        endTime = endTime,
        dates = dates,
        location = location,
        contactPhone = contactPhone,
        contactEmail = contactEmail,
        dressCode = dressCode
    )
}

fun CreateEventApi.toEntity(): Event {
    return Event(
        details = details.toEventDetails(),
        category = category.toEventCategory(),
        storageFolderId = storageFolderId,
        status = status.toEventStatus(),
        startTime = startTime,
        createdBy = createdBy,
        lastModifiedBy = lastModifiedBy,
    )
}

fun CreateEventDetailsApi.toEventDetails(): EventDetails {
    return EventDetails(
        title = title.toEventTitle(),
        description = description.toEventDescription(),
        startTime = startTime,
        endTime = endTime,
        dressCode = EventDressCode.CASUAL // 👈 Add this line
    )
}

fun EventTitleApi.toEventTitle(): EventTitle {
    return EventTitle(
        ro = ro,
        en = en,
        ru = ru
    )
}

fun EventDescriptionApi.toEventDescription(): EventDescription {
    return EventDescription(
        ro = ro,
        en = en,
        ru = ru
    )
}

fun EventStatus.toEventStatusApi(): EventStatusApi {
    return when (this) {
        EventStatus.DRAFT -> EventStatusApi.DRAFT
        EventStatus.PENDING_APPROVAL -> EventStatusApi.PENDING_APPROVAL
        EventStatus.PUBLISHED -> EventStatusApi.PUBLISHED
        EventStatus.APPLICATIONS_CLOSED -> EventStatusApi.APPLICATIONS_CLOSED
        EventStatus.FULL -> EventStatusApi.FULL
        EventStatus.IN_PROGRESS -> EventStatusApi.IN_PROGRESS
        EventStatus.COMPLETED -> EventStatusApi.COMPLETED
        EventStatus.CANCELLED -> EventStatusApi.CANCELLED
        EventStatus.POSTPONED -> EventStatusApi.POSTPONED
        EventStatus.REJECTED -> EventStatusApi.REJECTED
    }
}

@SuppressWarnings("CyclomaticComplexMethod")
fun EventCategory.toEventCategoryApi(): EventCategoryApi {
    return when (this) {
        EventCategory.SOCIAL -> EventCategoryApi.SOCIAL
        EventCategory.ANIMAL_CARE -> EventCategoryApi.ANIMAL_CARE
        EventCategory.CHILDREN_AND_YOUTH -> EventCategoryApi.CHILDREN_AND_YOUTH
        EventCategory.EDUCATION -> EventCategoryApi.EDUCATION
        EventCategory.ENVIRONMENT -> EventCategoryApi.ENVIRONMENT
        EventCategory.HEALTH -> EventCategoryApi.HEALTH
        EventCategory.DISABILITY_SUPPORT -> EventCategoryApi.DISABILITY_SUPPORT
        EventCategory.ELDERLY_CARE -> EventCategoryApi.ELDERLY_CARE
        EventCategory.DISASTER -> EventCategoryApi.DISASTER
        EventCategory.POVERTY -> EventCategoryApi.POVERTY
        EventCategory.CULTURE -> EventCategoryApi.CULTURE
        EventCategory.SPORT -> EventCategoryApi.SPORT
        EventCategory.FESTIVALS -> EventCategoryApi.FESTIVALS
        EventCategory.TECHNOLOGY -> EventCategoryApi.TECHNOLOGY
        EventCategory.BUSINESS -> EventCategoryApi.BUSINESS
        EventCategory.EMPLOYMENT -> EventCategoryApi.EMPLOYMENT
        EventCategory.SCIENCE -> EventCategoryApi.SCIENCE
        EventCategory.AGRICULTURE -> EventCategoryApi.AGRICULTURE
        EventCategory.CONSTRUCTION -> EventCategoryApi.CONSTRUCTION
        EventCategory.RELIGION -> EventCategoryApi.RELIGION
        EventCategory.HUMAN_RIGHTS -> EventCategoryApi.HUMAN_RIGHTS
        EventCategory.LEGAL -> EventCategoryApi.LEGAL
        EventCategory.SAFETY -> EventCategoryApi.SAFETY
        EventCategory.FAMILY -> EventCategoryApi.FAMILY
        EventCategory.LGBTQ_PLUS -> EventCategoryApi.LGBTQ_PLUS
        EventCategory.REFUGEE_SUPPORT -> EventCategoryApi.REFUGEE_SUPPORT
        EventCategory.INTERNATIONAL_VOLUNTEERING -> EventCategoryApi.INTERNATIONAL_VOLUNTEERING
        EventCategory.TOURISM -> EventCategoryApi.TOURISM
        EventCategory.HERITAGE -> EventCategoryApi.HERITAGE
        EventCategory.MEDIA -> EventCategoryApi.MEDIA
        EventCategory.GARDENING -> EventCategoryApi.GARDENING
        EventCategory.PEACE -> EventCategoryApi.PEACE
        EventCategory.ADDICTION_RECOVERY -> EventCategoryApi.ADDICTION_RECOVERY
        EventCategory.OTHER -> EventCategoryApi.OTHER
    }
}

fun EventStatusApi.toEventStatus(): EventStatus {
    return when (this) {
        EventStatusApi.DRAFT -> EventStatus.DRAFT
        EventStatusApi.PENDING_APPROVAL -> EventStatus.PENDING_APPROVAL
        EventStatusApi.PUBLISHED -> EventStatus.PUBLISHED
        EventStatusApi.APPLICATIONS_CLOSED -> EventStatus.APPLICATIONS_CLOSED
        EventStatusApi.FULL -> EventStatus.FULL
        EventStatusApi.IN_PROGRESS -> EventStatus.IN_PROGRESS
        EventStatusApi.COMPLETED -> EventStatus.COMPLETED
        EventStatusApi.CANCELLED -> EventStatus.CANCELLED
        EventStatusApi.POSTPONED -> EventStatus.POSTPONED
        EventStatusApi.REJECTED -> EventStatus.REJECTED
    }
}

@SuppressWarnings("CyclomaticComplexMethod")
fun EventCategoryApi.toEventCategory(): EventCategory {
    return when (this) {
        EventCategoryApi.SOCIAL -> EventCategory.SOCIAL
        EventCategoryApi.ANIMAL_CARE -> EventCategory.ANIMAL_CARE
        EventCategoryApi.CHILDREN_AND_YOUTH -> EventCategory.CHILDREN_AND_YOUTH
        EventCategoryApi.EDUCATION -> EventCategory.EDUCATION
        EventCategoryApi.ENVIRONMENT -> EventCategory.ENVIRONMENT
        EventCategoryApi.HEALTH -> EventCategory.HEALTH
        EventCategoryApi.DISABILITY_SUPPORT -> EventCategory.DISABILITY_SUPPORT
        EventCategoryApi.ELDERLY_CARE -> EventCategory.ELDERLY_CARE
        EventCategoryApi.DISASTER -> EventCategory.DISASTER
        EventCategoryApi.POVERTY -> EventCategory.POVERTY
        EventCategoryApi.CULTURE -> EventCategory.CULTURE
        EventCategoryApi.SPORT -> EventCategory.SPORT
        EventCategoryApi.FESTIVALS -> EventCategory.FESTIVALS
        EventCategoryApi.TECHNOLOGY -> EventCategory.TECHNOLOGY
        EventCategoryApi.BUSINESS -> EventCategory.BUSINESS
        EventCategoryApi.EMPLOYMENT -> EventCategory.EMPLOYMENT
        EventCategoryApi.SCIENCE -> EventCategory.SCIENCE
        EventCategoryApi.AGRICULTURE -> EventCategory.AGRICULTURE
        EventCategoryApi.CONSTRUCTION -> EventCategory.CONSTRUCTION
        EventCategoryApi.RELIGION -> EventCategory.RELIGION
        EventCategoryApi.HUMAN_RIGHTS -> EventCategory.HUMAN_RIGHTS
        EventCategoryApi.LEGAL -> EventCategory.LEGAL
        EventCategoryApi.SAFETY -> EventCategory.SAFETY
        EventCategoryApi.FAMILY -> EventCategory.FAMILY
        EventCategoryApi.LGBTQ_PLUS -> EventCategory.LGBTQ_PLUS
        EventCategoryApi.REFUGEE_SUPPORT -> EventCategory.REFUGEE_SUPPORT
        EventCategoryApi.INTERNATIONAL_VOLUNTEERING -> EventCategory.INTERNATIONAL_VOLUNTEERING
        EventCategoryApi.TOURISM -> EventCategory.TOURISM
        EventCategoryApi.HERITAGE -> EventCategory.HERITAGE
        EventCategoryApi.MEDIA -> EventCategory.MEDIA
        EventCategoryApi.GARDENING -> EventCategory.GARDENING
        EventCategoryApi.PEACE -> EventCategory.PEACE
        EventCategoryApi.ADDICTION_RECOVERY -> EventCategory.ADDICTION_RECOVERY
        EventCategoryApi.OTHER -> EventCategory.OTHER
    }
}

fun EventFile.toEventFileResponseApi(): EventFileApi {
    return EventFileApi(
        id = id ?: error(IllegalStateException("File id is null")),
        eventId = eventId,
        type = type.toEventFileTypeApi(),
        storageFileId = storageFileId,
        originalName = originalName,
        contentType = contentType,
        size = size,
        index = index,
        createdAt = createdAt,
    )
}

fun EventFileType.toEventFileTypeApi(): EventFileTypeApi {
    return when (this) {
        EventFileType.ATTACHMENT -> EventFileTypeApi.ATTACHMENT
        EventFileType.COVER -> EventFileTypeApi.COVER
        EventFileType.GALLERY -> EventFileTypeApi.GALLERY
    }
}
