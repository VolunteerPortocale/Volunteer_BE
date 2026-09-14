package com.example.exception

import org.springframework.http.HttpStatus

class ResourceNotFoundException(
    val resourceType: String,
    val resourceId: String
) : BusinessException(
    message = "$resourceType not found with id: $resourceId",
    status = HttpStatus.NOT_FOUND,
    errorCode = "RESOURCE_NOT_FOUND"
) {
    constructor(message: String) : this("Resource", message)
}
