import arrow.core.Validated
import arrow.core.handleError
import arrow.core.invalidNel
import arrow.core.traverseValidated
import arrow.core.valid

data class EmailRoute private constructor(
    val from: Email,
    val to: Email,
    val cc: List<Email> = emptyList(),
    val bcc: List<Email> = emptyList()
) {
    companion object {
        fun emailRoute(
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
                if (emailRoute.cc.isEmpty() && emailRoute.bcc.isEmpty()) {
                    ValidationError("Both cc and bcc are empty").invalidNel()
                } else {
                    emailRoute.valid()
                }
            }
    }
}



fun main() {
    val validatedEmailRoute =
        EmailRoute.emailRoute("no-reply@localhost", "test@localhost", listOf("hr@localhost"))

    validatedEmailRoute.map { emailRoute ->
        //here we could do something useful
        println(emailRoute)
        //EmailRoute(from=Email(value=no-reply@localhost), to=Email(value=test@localhost), cc=[Email(value=hr@localhost)], bcc=[])
    }

    //let's make our cc/bcc validation fail
    EmailRoute.emailRoute("no-reply@localhost", "test@localhost")
        .handleError { errors ->
            println(errors)
            //NonEmptyList(ValidationError(message=Both cc and bcc are empty))
        }

    //let's accumulate some errors
    EmailRoute.emailRoute("sender", "receiver", listOf("cc1", "valid@local"), listOf("bcc1", "bcc2"))
        .handleError { errors ->
            println(errors)
            //NonEmptyList(ValidationError(message='sender' should be a valid email address),
            //    ValidationError(message='receiver' should be a valid email address),
            //    ValidationError(message='cc1' should be a valid email address),
            //    ValidationError(message='bcc1' should be a valid email address),
            //    ValidationError(message='bcc2' should be a valid email address))
        }
}
