package com.g0dkar.samplek8sproj.api.global

import com.g0dkar.samplek8sproj.extensions.log
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalApiExceptionHandler {
    @ExceptionHandler(Throwable::class)
    fun handleThrowable(e: Throwable): ResponseEntity<Any> {
        log.warn("process=unexpected_error, status=error", e)

        return ResponseEntity
            .status(INTERNAL_SERVER_ERROR)
            .body("Unexpected error. Please, try again later".toByteArray())
    }
}
