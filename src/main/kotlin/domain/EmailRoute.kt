package domain

import arrow.core.Validated
import arrow.core.andThen
import arrow.core.invalidNel
import arrow.core.traverseValidated
import arrow.core.valid
import validate

data class EmailRoute private constructor(
    val from: Email,
    val to: Email,
    val cc: List<Email> = emptyList(),
    val bcc: List<Email> = emptyList()
) {
    companion object {
        fun validated(
            allowedSenders: AllowedSenders,
            receiveEmailConsents: ReceiveEmailConsents,
            from: String,
            to: String,
            cc: List<String> = emptyList(),
            bcc: List<String> = emptyList()
        ): Validated<ValidationErrors, EmailRoute> =
            validate(
                Email.valueOf(from),
                Email.valueOf(to),
                cc.traverseValidated { Email.valueOf(it) },
                bcc.traverseValidated { Email.valueOf(it) }
            ) { validFrom, validTo, validCc, validBcc ->
                EmailRoute(validFrom, validTo, validCc, validBcc)
            }.andThen { emailRoute ->
                when {
                    emailRoute.cc.isEmpty() && emailRoute.bcc.isEmpty() ->
                        ValidationError("Both cc and bcc are empty").invalidNel()
                    emailRoute.from !in allowedSenders ->
                        ValidationError("'${emailRoute.from}' is not in the list of allowed senders").invalidNel()
                    emailRoute.to !in receiveEmailConsents ->
                        ValidationError("'${emailRoute.to}' does not consent receiving emails").invalidNel()
                    else -> emailRoute.valid()
                }
            }
    }
}
