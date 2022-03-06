package domain

import arrow.core.Validated
import arrow.core.invalidNel
import arrow.core.valid

@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        fun valueOf(value: String?): Validated<ValidationErrors, Email> =
            when {
                value == null -> ValidationError("email should not be null").invalidNel()
                isValidEmail(value) -> Email(value).valid()
                else -> ValidationError("'$value' should be a valid email address").invalidNel()
            }

        private fun isValidEmail(value: String): Boolean = value.contains('@')
    }

    override fun toString(): String = value
}
