package domain

import arrow.core.NonEmptyList

sealed class ApplicationError(open val message: String?) {
    override fun toString(): String = message ?: "null"
}

typealias ApplicationErrors = NonEmptyList<ApplicationError>

data class ValidationError(override val message: String) : ApplicationError(message)
typealias ValidationErrors = NonEmptyList<ValidationError>

data class RuntimeError(override val message: String?) : ApplicationError(message)

object InterruptedError : ApplicationError("Program stopped!")
