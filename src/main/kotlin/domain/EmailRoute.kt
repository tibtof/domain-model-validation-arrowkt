package domain

import arrow.core.Validated
import arrow.core.andThen
import arrow.core.invalidNel
import arrow.core.traverseValidated
import arrow.core.valid
import validate

typealias CreateValidatedEmailRoute = (String, String, List<String?>, List<String?>) -> Validated<ValidationErrors, EmailRoute>

data class EmailRoute private constructor(
    val from: Email,
    val to: Email,
    val cc: List<Email> = emptyList(),
    val bcc: List<Email> = emptyList()
) {
    companion object {
        fun factoryWithContext(allowedSenders: AllowedSenders, receiveEmailConsents: ReceiveEmailConsents):
                CreateValidatedEmailRoute =
            { from, to, cc, bcc ->
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
                            ValidationError("'${emailRoute.from.value}' is not in the list of allowed senders").invalidNel()
                        emailRoute.to !in receiveEmailConsents ->
                            ValidationError("'${emailRoute.to.value}' does not consent receiving emails").invalidNel()
                        else -> emailRoute.valid()
                    }
                }
            }
    }
}
