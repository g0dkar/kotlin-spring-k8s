package com.g0dkar.samplek8sproj.api.global

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.g0dkar.samplek8sproj.extensions.log
import com.g0dkar.samplek8sproj.model.response.ApiError
import com.g0dkar.samplek8sproj.model.response.ValidationApiError
import com.g0dkar.samplek8sproj.model.response.ValidationApiErrorDescription
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebInputException

/**
 * Intercepts exceptions thrown by API invocations and turns them in [ApiError] responses.
 */
@RestControllerAdvice
class GlobalApiExceptionHandler {
    // @ExceptionHandler(HttpMessageNotReadableException::class)
    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(exception: ServerWebInputException): ResponseEntity<ApiError> =
        log.warn("process=error_handling, status=invalid_request", exception)
            .let {
                val cause = exception.cause

                val payload = when (cause != null && cause is MismatchedInputException) {
                    true -> ValidationApiError(
                        status = BAD_REQUEST.value(),
                        code = cause.targetType?.simpleName ?: BAD_REQUEST.name,
                        message = cause.originalMessage?.takeIf { it.isNotBlank() } ?: BAD_REQUEST.reasonPhrase,
                        validationErrors = listOf(ValidationApiErrorDescription.of(cause))
                    )
                    false -> ApiError.of(exception, BAD_REQUEST)
                }

                return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(payload)
            }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException): ResponseEntity<ValidationApiError> =
        log.warn("process=error_handling, status=validation_errors")
            .let {
                val errors = exception.bindingResult.allErrors.map {
                    ValidationApiErrorDescription.of(it)
                }

                return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(ValidationApiError.of(errors))
            }

    @ExceptionHandler(Throwable::class)
    fun handleThrowable(throwable: Throwable): ResponseEntity<ApiError> =
        log.warn("process=error_handling, status=unexpected_error", throwable)
            .let { ApiError.of(throwable) }
            .let { ResponseEntity.status(it.status).body(it) }
}
