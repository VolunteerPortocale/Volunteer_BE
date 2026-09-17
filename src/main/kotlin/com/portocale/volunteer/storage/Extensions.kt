package com.portocale.volunteer.storage

import com.google.api.services.drive.model.File

fun File.toFileReference(contentType: String): FileReference {
    return FileReference(
        fileId = id,
        name = name,
        contentType = mimeType ?: contentType,
        size = size.toLong(),
    )
}
