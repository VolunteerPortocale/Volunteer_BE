package com.portocale.volunteer.notification.service

import com.portocale.volunteer.notification.EmailLog
import com.portocale.volunteer.notification.EmailStatus
import com.portocale.volunteer.notification.repository.EmailLogRepository
import jakarta.mail.internet.MimeMessage
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import java.util.Locale

@Service
class EmailServiceImpl(
    private val mailSender: JavaMailSender,
    private val velocityEngine: VelocityEngine,
    private val messageResolver: MessageResolver,
    private val emailLogRepository: EmailLogRepository,
    @Value("\${spring.mail.sender}")
    private val defaultFromAddress: String
) : EmailService {

    private val log = LoggerFactory.getLogger(EmailServiceImpl::class.java)

    override fun sendSimpleEmail(to: String, subject: String, content: String, isHtml: Boolean) {
        dispatchEmail(
            to = to,
            subject = subject,
            content = content,
            isHtml = isHtml,
            templateName = null
        )
    }

    override fun sendRegistrationEmail(to: String, firstName: String, otp: String) {
        println("Not implemented yet")
    }

    override fun sendTemplatedEmail(
        to: String,
        subject: String,
        templateName: String,
        templateModel: Map<String, Any>,
        locale: Locale
    ) {
        val renderedContent = renderTemplate(templateName, templateModel, locale)
        dispatchEmail(
            to = to,
            subject = subject,
            content = renderedContent,
            isHtml = true,
            templateName = templateName
        )
    }

    private fun renderTemplate(templateName: String, model: Map<String, Any>, locale: Locale): String {
        val normalizedTemplate = if (templateName.endsWith(".vm")) templateName else "$templateName.vm"
        val templatePath = if (normalizedTemplate.startsWith("templates/")) {
            normalizedTemplate
        } else {
            "templates/$normalizedTemplate"
        }

        val context = VelocityContext().apply {
            put("messages", messageResolver)
            put("locale", locale)
            model.forEach { (key, value) -> put(key, value) }
        }

        val writer = StringWriter()
        velocityEngine.mergeTemplate(templatePath, StandardCharsets.UTF_8.name(), context, writer)
        return writer.toString()
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    private fun dispatchEmail(
        to: String,
        subject: String,
        content: String,
        isHtml: Boolean,
        templateName: String?
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
            helper.setText(content, isHtml)

            mailSender.send(message)
            log.info("Email sent successfully to: {} with subject: '{}'", to, subject)
        } catch (ex: Exception) {
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
