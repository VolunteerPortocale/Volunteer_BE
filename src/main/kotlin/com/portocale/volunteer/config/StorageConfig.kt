package com.portocale.volunteer.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "google.drive")
data class StorageConfig(
    val applicationName: String,
    val rootFolderId: String? = null,
    val credentialsPath: String,
    val tokensPath: String
)
