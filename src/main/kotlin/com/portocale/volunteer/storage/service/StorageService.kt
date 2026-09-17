package com.portocale.volunteer.storage.service

import com.portocale.volunteer.storage.FileReference
import org.springframework.web.multipart.MultipartFile
import java.io.OutputStream

interface StorageService {
    fun upload(
        file: MultipartFile,
        folderId: String
    ): FileReference
    fun getOrCreateRootFolderId(): String
    fun createFolder(
        name: String,
        parentFolderId: String
    ): String
    fun download(
        fileId: String,
        outputStream: OutputStream
    )
    fun delete(fileId: String)

    fun deleteFolder(folderId: String)
}
