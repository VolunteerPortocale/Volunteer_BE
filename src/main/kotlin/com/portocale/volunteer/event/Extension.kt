@file:Suppress("TooManyFunctions")

package com.portocale.volunteer.event

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.graphql.model.DressCodeGQL
import com.portocale.volunteer.graphql.model.EventDurationGQL
import com.portocale.volunteer.graphql.model.EventGQL
import com.portocale.volunteer.graphql.model.EventTypeGQL


fun Event.toEventApi(language: LanguageApi = LanguageApi.RO): EventApi {
    return EventApi(
        id = id ?: error(IllegalStateException("ID is null")),
        details = details.toEventDetailsApi(language),
        category = category.toEventCategoryApi(),
        storageFolderId = storageFolderId ?: "",
        status = status.toEventStatusApi(),
        startTime = startTime,
        createdAt = createdAt,
        createdBy = createdBy,
        lastModifiedAt = lastModifiedAt,
        lastModifiedBy = lastModifiedBy,
        location = location,
        nrVolunteers = nrVolunteers,
        startDate = startDate,
        endDate = endDate,
        dates = dates,
        time = time,
        coverImage = coverImage,
        images = images,
        eventType = eventType,
        dressCode = dressCode,
        duration = duration,
        contactPhone = contactPhone,
        contactEmail = contactEmail
    )
}

fun EventApi.toEventGQL(): EventGQL = EventGQL(
    id,
    details.title,
    details.description,
    location.orEmpty(),
    nrVolunteers ?: 0,
    startDate ?: startTime.toString(),
    endDate ?: details.endTime?.toString(),
    dates ?: emptyList(),
    time,
    coverImage,
    images ?: emptyList(),
    eventType?.let { runCatching { EventTypeGQL.valueOf(it) }.getOrNull() } ?: EventTypeGQL.OTHER,
    dressCode?.let { runCatching { DressCodeGQL.valueOf(it) }.getOrNull() } ?: DressCodeGQL.CASUAL,
    duration?.let { runCatching { EventDurationGQL.valueOf(it) }.getOrNull() } ?: EventDurationGQL.ONE_DAY,
    contactEmail.orEmpty(),
    contactPhone.orEmpty()
)

fun EventDetails.toEventDetailsApi(language: LanguageApi): EventDetailsApi {
    return EventDetailsApi(
        title = title.translated(language),
        description = description.translated(language),
        startTime = startTime,
        endTime = endTime,
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
