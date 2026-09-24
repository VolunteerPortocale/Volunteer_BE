package com.portocale.volunteer.enrollment.controller

import com.portocale.volunteer.config.toLanguageApi
import com.portocale.volunteer.enrollment.service.EnrollmentService
import com.portocale.volunteer.enrollment.toCreateEnrollmentApi
import com.portocale.volunteer.enrollment.toEnrollmentGql
import com.portocale.volunteer.graphql.model.CreateEnrollmentInputGQL
import com.portocale.volunteer.graphql.model.EnrollmentGQL
import java.util.Locale
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
class EnrollmentController(
    private val enrollmentService: EnrollmentService
) {
    @MutationMapping
    fun enroll(
        @Argument input: CreateEnrollmentInputGQL,
        locale: Locale
    ): EnrollmentGQL {
        return enrollmentService.enroll(
            request = input.toCreateEnrollmentApi(),
            language = locale.toLanguageApi()
        ).toEnrollmentGql()
    }
}
