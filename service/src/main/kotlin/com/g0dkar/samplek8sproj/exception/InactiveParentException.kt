package com.g0dkar.samplek8sproj.exception

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(BAD_REQUEST)
class InactiveParentException : RuntimeException()
