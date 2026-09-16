package com.portocale.volunteer.config

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(StorageConfig::class)
class GoogleDriveConfiguration(
    private val properties: StorageConfig
) {

    private val jsonFactory = GsonFactory.getDefaultInstance()

    @Bean
    fun googleDrive(): Drive {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

        return Drive.Builder(
            httpTransport,
            jsonFactory,
            authorize(httpTransport)
        )
            .setApplicationName(properties.applicationName)
            .build()
    }

    private fun authorize(
        httpTransport: NetHttpTransport
    ): Credential {
        val clientSecrets = FileInputStream(properties.credentialsPath).use { input ->
            GoogleClientSecrets.load(
                jsonFactory,
                InputStreamReader(input)
            )
        }

        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport,
            jsonFactory,
            clientSecrets,
            listOf(DriveScopes.DRIVE_FILE)
        )
            .setDataStoreFactory(
                FileDataStoreFactory(File(properties.tokensPath))
            )
            .setAccessType("offline")
            .build()

        val receiver = LocalServerReceiver.Builder()
            .setPort(8888)
            .build()

        return AuthorizationCodeInstalledApp(
            flow,
            receiver
        ).authorize("user")
    }
}
