package com.g0dkar.samplek8sproj.model.response

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime
import javax.validation.ConstraintViolation

/**
 * Represents a generic API Error.
 *
 * Defaults to [HttpStatus.INTERNAL_SERVER_ERROR].
 *
 * Base class for Api Errors.
 */
open class ApiError(
    /** Error HTTP Code */
    val status: Int = HttpStatus.INTERNAL_SERVER_ERROR.value(),
    /** Error Message */
    val message: String = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
    /** A code for this error (to be easily interpreted by software) */
    val code: String? = HttpStatus.INTERNAL_SERVER_ERROR.name
) {
    /** When this error occurred */
    val timestamp: OffsetDateTime = OffsetDateTime.now()
    /** Points to a page with a bit more info about this message status code */
    val statusInfo = "$HTTP_STATUS_PAGE/$status"

    companion object {
        private const val HTTP_STATUS_PAGE = "https://developer.mozilla.org/en-US/docs/Web/HTTP/Status"

        fun of(exception: Throwable, status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR): ApiError {
            val responseCodeAnnotation = exception.javaClass.getAnnotation(ResponseStatus::class.java)
            val actualStatus = responseCodeAnnotation?.code
                ?: responseCodeAnnotation?.value
                ?: if (exception is ResponseStatusException) {
                    exception.status
                } else {
                    status
                }

            return ApiError(
                status = actualStatus.value(),
                message = exception.message?.takeIf { it.isNotBlank() } ?: actualStatus.reasonPhrase,
                code = exception.javaClass.simpleName
            )
        }

        fun of(status: HttpStatus): ApiError {
            return ApiError(
                status = status.value(),
                message = status.reasonPhrase,
                code = status.name
            )
        }
    }
}

/**
 * Represents a Validation API Error. It'll contain the list of validations that failed.
 */
class ValidationApiError(
    status: Int,
    message: String,
    code: String?,
    val validationErrors: List<ValidationApiErrorDescription>
) : ApiError(status, message, code) {
    companion object {
        fun of(
            validationErrors: List<ValidationApiErrorDescription>,
            status: HttpStatus = HttpStatus.BAD_REQUEST
        ): ValidationApiError =
            ValidationApiError(
                status = status.value(),
                message = status.reasonPhrase,
                code = status.name,
                validationErrors = validationErrors
            )
    }
}

/**
 * A single validation error. A basic representation of a [ConstraintViolation] or [FieldError].
 */
data class ValidationApiErrorDescription(
    val field: String,
    val type: String?,
    val message: String?
) {
    companion object {
        /**
         * Builds a [ValidationApiErrorDescription] of a [ObjectError].
         */
        fun of(error: ObjectError): ValidationApiErrorDescription =
            ValidationApiErrorDescription(
                field = when (error is FieldError) {
                    true -> error.field
                    false -> buildField(error)
                },
                type = error.code,
                message = error.defaultMessage
            )

        /**
         * Builds a [ValidationApiErrorDescription] of a [MismatchedInputException].
         */
        fun of(inputException: MismatchedInputException): ValidationApiErrorDescription =
            ValidationApiErrorDescription(
                inputException.path.joinToString("") {
                    if (it.fieldName != null) {
                        ".${it.fieldName}"
                    } else {
                        "[${it.index}]"
                    }
                }.substring(1),
                inputException.targetType?.simpleName,
                inputException.originalMessage ?: ""
            )

        /**
         * Builds a field name of an [ObjectError].
         */
        private fun buildField(objectError: ObjectError): String {
            val code = objectError.code
            val codes = objectError.codes

            return if (code != null) {
                codes?.get(2)?.substring(code.length + 1)
                    ?: codes?.get(0)?.substring(code.length + 1)
                    ?: ""
            } else {
                codes?.get(0) ?: ""
            }
        }
    }
}
