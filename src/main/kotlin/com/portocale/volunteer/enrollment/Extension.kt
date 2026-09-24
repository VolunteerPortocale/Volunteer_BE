package com.portocale.volunteer.enrollment

import com.portocale.volunteer.graphql.model.CreateEnrollmentInputGQL
import com.portocale.volunteer.graphql.model.EnrollmentGQL
import com.portocale.volunteer.graphql.model.EnrollmentStatusGQL
import java.time.Instant


fun CreateEnrollmentApi.toEntity(userId: String): Enrollment {
    return Enrollment(
        id = null,
        eventId = eventId,
        userId = userId,
        enrolledAt = Instant.now(),
        status = EnrollmentStatus.CONFIRMED,
        statusHistory = emptyList()
    )
}

fun Enrollment.toEnrollmentResponseApi(): EnrollmentResponseApi {
    return EnrollmentResponseApi(
        id = id ?: error(IllegalStateException("Enrollment ID is null")),
        eventId = eventId,
        userId = userId,
        status = status.name
    )
}

fun EnrollmentResponseApi.toEnrollmentGql(): EnrollmentGQL {
    return EnrollmentGQL(
        id,
        eventId,
        userId,
        EnrollmentStatusGQL.valueOf(status)
    )
}

fun CreateEnrollmentInputGQL.toCreateEnrollmentApi(): CreateEnrollmentApi {
    return CreateEnrollmentApi(
        eventId = eventId
    )
}

