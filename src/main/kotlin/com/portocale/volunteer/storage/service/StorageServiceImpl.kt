package com.portocale.volunteer.storage.service

import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.portocale.volunteer.storage.FileReference
import com.portocale.volunteer.storage.toFileReference
import java.io.OutputStream
import java.nio.file.Paths
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import com.google.api.services.drive.model.File as DriveFile

@Service
class StorageServiceImpl(
    private val drive: Drive
) : StorageService {


    override fun upload(
        file: MultipartFile,
        folderId: String
    ): FileReference {

        val fileName = sanitizeFileName(
            file.originalFilename ?: DEFAULT_FILE_NAME
        )

        val contentType =
            file.contentType ?: MediaType.APPLICATION_OCTET_STREAM_VALUE

        val metadata = DriveFile().apply {
            name = fileName
            parents = listOf(folderId)
        }

        return file.inputStream.use { inputStream ->

            val content = InputStreamContent(
                contentType,
                inputStream
            ).apply {
                length = file.size
            }

            val uploadedFile = drive.files()
                .create(metadata, content)
                .setSupportsAllDrives(true)
                .setFields("id,name,mimeType,size")
                .execute()

            uploadedFile.toFileReference(contentType)
        }
    }

    override fun getOrCreateRootFolderId(): String {
        val folders = drive.files()
            .list()
            .setQ(
                "name = '${drive.applicationName}' " +
                        "and mimeType = 'application/vnd.google-apps.folder' " +
                        "and trashed = false"
            )
            .setSpaces("drive")
            .setFields("files(id, name)")
            .execute()
            .files

        return folders.firstOrNull()?.id
            ?: createRootFolder()
    }

    override fun createFolder(
        name: String,
        parentFolderId: String
    ): String {
        val folder = File().apply {
            this.name = name
            mimeType = "application/vnd.google-apps.folder"
            parents = listOf(parentFolderId)
        }

        return drive.files()
            .create(folder)
            .setFields("id")
            .execute()
            .id
    }

    override fun download(
        fileId: String,
        outputStream: OutputStream
    ) {
        drive.files()
            .get(fileId)
            .setSupportsAllDrives(true)
            .executeMediaAndDownloadTo(outputStream)
    }

    override fun delete(fileId: String) {
        drive.files()
            .delete(fileId)
            .setSupportsAllDrives(true)
            .execute()
    }

    override fun deleteFolder(folderId: String) {
        delete(folderId)
    }

    private fun sanitizeFileName(fileName: String): String =
        Paths.get(fileName)
            .fileName
            .toString()

    companion object {
        private const val DEFAULT_FILE_NAME = "file"

        private const val GOOGLE_DRIVE_FOLDER_MIME_TYPE =
            "application/vnd.google-apps.folder"
    }

    private fun createRootFolder(): String {
        val folder = File().apply {
            name = drive.applicationName
            mimeType = "application/vnd.google-apps.folder"
        }

        return drive.files()
            .create(folder)
            .setFields("id")
            .execute()
            .id
    }
}
