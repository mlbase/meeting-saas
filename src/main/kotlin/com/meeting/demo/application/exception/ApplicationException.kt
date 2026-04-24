package com.meeting.demo.application.exception

abstract class ApplicationException(message: String) : RuntimeException(message)

class ResourceNotFoundException(message: String) : ApplicationException(message)
class AlreadyExistsException(message: String) : ApplicationException(message)
