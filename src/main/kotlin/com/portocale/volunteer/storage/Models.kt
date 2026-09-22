package com.portocale.volunteer.storage

data class FileReference(
    val fileId: String,
    val name: String,
    val contentType: String,
    val size: Long
)

data class PlaceholderStorage(
    val fileId: String
)
