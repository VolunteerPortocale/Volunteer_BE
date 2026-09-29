package com.portocale.volunteer.enrollment

import com.portocale.volunteer.graphql.model.CreateEnrollmentInputGQL
import com.portocale.volunteer.graphql.model.EnrollmentGQL
import com.portocale.volunteer.graphql.model.EnrollmentStatusGQL
import com.portocale.volunteer.graphql.model.GenericPayloadGQL
import com.portocale.volunteer.graphql.model.GenericStatusGQL
import io.micrometer.core.instrument.binder.http.HttpJakartaServletRequestTags.status
import java.time.Instant


fun CreateEnrollmentApi.toEntity(userId: String): Enrollment {
    return Enrollment(
        id = null,
        eventId = eventId,
        userId = userId,
        status = EnrollmentStatus.ENROLLED
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

fun GenericStatusApi.toGenericPayloadGQL(): GenericPayloadGQL {
    return GenericPayloadGQL(
        when (this) {
            GenericStatusApi.OK -> GenericStatusGQL.OK
            GenericStatusApi.NOK -> GenericStatusGQL.NOK
        }
    )
}

