package com.meeting.demo.domain.exception

abstract class DomainException(message: String) : RuntimeException(message)

class InvalidStateTransitionException(message: String) : DomainException(message)
class UnauthorizedActionException(message: String) : DomainException(message)
class DuplicateActiveMeetingException(message: String) : DomainException(message)
