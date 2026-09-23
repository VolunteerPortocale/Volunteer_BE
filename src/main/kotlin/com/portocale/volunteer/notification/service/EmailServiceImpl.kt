package com.portocale.volunteer.notification.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.notification.EmailLog
import com.portocale.volunteer.notification.EmailStatus
import com.portocale.volunteer.notification.EmailSubject
import com.portocale.volunteer.notification.TemplateKeys
import com.portocale.volunteer.notification.TemplateName
import com.portocale.volunteer.notification.repository.EmailLogRepository
import com.portocale.volunteer.qr.service.QrService
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils.IMAGE_PNG_VALUE

private const val HEADER_LOGO_PATH = "templates/assets/favicon.ico"
private const val X_ICON_CONTENT_TYPE = "image/x-icon"

@Service
class EmailServiceImpl(
    private val mailSender: JavaMailSender,
    private val velocityEngine: VelocityEngine,
    private val messageResolver: MessageResolver,
    private val emailLogRepository: EmailLogRepository,
    @Value("\${spring.mail.sender}")
    private val defaultFromAddress: String,
    private val qrService: QrService
) : EmailService {

    private val log = LoggerFactory.getLogger(EmailServiceImpl::class.java)

    override fun sendEnrollmentConfirmation(
        eventId: String,
        userId: String,
        email: String,
        language: LanguageApi
    ) {
        val templateName = TemplateName.ENROLLMENT_CONFIRMATION.value
        val qrBytes = qrService.generateVolunteerPresenceConfirmationQr(eventId, userId)

        val content = renderContent(
            templateName = templateName,
            language = language
        )

        dispatchEmail(
            to = email,
            subject = EmailSubject.ENROLLMENT_CONFIRMATION.value,
            content = content,
            templateName = templateName,
            inlineImages = mapOf(TemplateKeys.QR_CODE.value to qrBytes)
        )
    }

    override fun sendRegistrationEmail(to: String, firstName: String, otp: String, language: LanguageApi) {
        val templateName = TemplateName.REGISTRATION.value

        val content = renderContent(
            templateName = templateName,
            language = language,
            model = mapOf(TemplateKeys.OTP.value to otp)
        )

        dispatchEmail(
            to = to,
            subject = EmailSubject.REGISTRATION.value,
            content = content,
            templateName = templateName
        )
    }

    private fun renderContent(templateName: String, model: Map<String, Any>? = null, language: LanguageApi): String {
        val templatePath = "templates/$templateName.vm"

        val context = VelocityContext().apply {
            put(TemplateKeys.MESSAGES.value, messageResolver)
            put(TemplateKeys.LOCALE.value, language.value)
            model?.forEach { (key, value) -> put(key, value) }
        }

        val writer = StringWriter()
        velocityEngine.mergeTemplate(templatePath, StandardCharsets.UTF_8.name(), context, writer)

        return writer.toString()
    }

    private fun dispatchEmail(
        to: String,
        subject: String,
        content: String,
        templateName: String,
        inlineImages: Map<String, ByteArray> = emptyMap()
    ) {
        var status = EmailStatus.SENT
        var errorMessage: String? = null

        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
            )

            helper.setFrom(defaultFromAddress)
            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(content, true)

            inlineImages.forEach { (contentId, bytes) ->
                helper.addInline(contentId, ByteArrayResource(bytes), IMAGE_PNG_VALUE)
            }

            val logoResource = ClassPathResource(HEADER_LOGO_PATH)
            if (logoResource.exists()) {
                helper.addInline(TemplateKeys.APP_LOGO.value, logoResource, X_ICON_CONTENT_TYPE)
            }

            mailSender.send(message)
            log.info("Email sent successfully to: {} with subject: '{}'", to, subject)
        } catch (ex: MessagingException) {
            status = EmailStatus.FAILED
            errorMessage = ex.message
            log.error("Failed to send email to: {} with subject: '{}'. Error: {}", to, subject, ex.message, ex)
            throw ex
        } finally {
            emailLogRepository.save(
                EmailLog(
                    recipient = to,
                    subject = subject,
                    content = content,
                    templateName = templateName,
                    status = status,
                    errorMessage = errorMessage
                )
            )
        }
    }
}
