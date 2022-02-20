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
}

@JvmInline
value class WhitelistedEmail private constructor(private val email: Email) {
    companion object {
        fun whitelistedEmail(whitelist: List<Email>, value: String?): Validated<ValidationErrors, WhitelistedEmail> {
            val email = Email.valueOf(value)
            return email.andThen {
                if (it !in whitelist) ValidationError("'$it' is not in the list of whitelisted emails").invalidNel()
                else WhitelistedEmail(it).valid()
            }
        }
    }

    val value
        get() = email.value
}
