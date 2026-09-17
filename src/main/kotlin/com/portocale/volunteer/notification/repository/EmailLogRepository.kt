package com.portocale.volunteer.notification.repository

import com.portocale.volunteer.notification.EmailLog
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailLogRepository : MongoRepository<EmailLog, String>
