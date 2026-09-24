package com.portocale.volunteer.enrollment

import com.portocale.volunteer.graphql.model.CreateEnrollmentInputGQL
import com.portocale.volunteer.graphql.model.EnrollmentGQL
import com.portocale.volunteer.graphql.model.EnrollmentStatusGQL

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