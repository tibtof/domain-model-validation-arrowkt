//import arrow.core.Invalid
//import arrow.core.NonEmptyList
//import arrow.core.Valid
//import arrow.core.Validated
//import arrow.core.ValidatedNel
//import arrow.core.invalid
//import arrow.core.invalidNel
//import arrow.core.traverseValidated
//import arrow.core.valid
//import arrow.core.zip
//import arrow.typeclasses.Semigroup
//
//interface Error
//
//data class ValidationError(val message: String) : Error {
//    companion object {
//        operator fun invoke(lazyMessage: () -> String) = ValidationError(lazyMessage())
//    }
//}
//
//typealias ValidationErrors = NonEmptyList<ValidationError>
//
//@JvmInline
//value class Email private constructor(val value: String) {
//    companion object {
//        fun valueOf(value: String): Validated<ValidationErrors, Email> =
//            if (isValidEmail(value)) Email(value).valid()
//            else ValidationError { "'$value' should be a valid email address" }.invalidNel()
//
//        private fun isValidEmail(value: String) =
//            value.contains('@')
//    }
//}
//
//fun mainWhen() {
//    val from: Validated<ValidationErrors, Email> = Email.valueOf("no-reply@localhost")
//    val to: Validated<ValidationErrors, Email> = Email.valueOf("test@localhost")
//
//    val validatedEmailRoute: Validated<ValidationErrors, EmailRoute> = when {
//        //both emails are valid, we can construct an EmailRoute
//        from is Validated.Valid && to is Validated.Valid ->
//            //smart cast: validated*.value are of type Email
//            EmailRoute(from.value, to.value).valid()
//        //only one of the emails is invalid, we return directly the invalid object
//        //remember, Invalid<E> has a single type param, and it can substitute both
//        //Validated<ValidationErrors, Email> and Validated<ValidationErrors, EmailRoute>
//        from is Invalid && to is Valid -> from
//        from is Valid && to is Invalid -> to
//        //both emails are invalid, we have to concatenate the errors
//        from is Invalid && to is Invalid ->
//            //smart cast: validated*.value are of type ValidationErrors
//            (to.value + from.value).invalid()
//        else -> TODO("we covered all the cases, but the compiler doesn't know")
//    }.tap(::println)
//    //EmailRoute(from=Email(value=no-reply@localhost), to=Email(value=test@localhost), cc=[], bcc=[])
//}
//
//
//fun mainWithZip() {
//    val from: Validated<ValidationErrors, Email> = Email.valueOf("no-reply@localhost")
//    val to: Validated<ValidationErrors, Email> = Email.valueOf("test@localhost")
//
//    val validatedEmailRoute: Validated<ValidationErrors, EmailRoute> =
//        from.zip(Semigroup.nonEmptyList(), to) { validFrom: Email, validTo: Email ->
//            //we only get here if both emails are valid
//            EmailRoute(validFrom, validTo)
//        }.tap(::println)
//    //EmailRoute(from=Email(value=no-reply@localhost), to=Email(value=test@localhost), cc=[], bcc=[])
//}
//
//
//fun main() {
//    val from: Validated<ValidationErrors, Email> = Email.valueOf("no-reply@localhost")
//    val to: Validated<ValidationErrors, Email> = Email.valueOf("test@localhost")
//
//    val validatedEmailRoute: Validated<ValidationErrors, EmailRoute> =
//        from.zip(Semigroup.nonEm ptyList (), to) { validFrom, validTo ->
//            EmailRoute(validFrom, validTo)
//        }.tap(::println)
//    //EmailRoute(from=Email(value=no-reply@localhost), to=Email(value=test@localhost), cc=[], bcc=[])
//}
//
//fun <E, A, B, R> validate(
//    a: ValidatedNel<E, A>,
//    b: ValidatedNel<E, B>,
//    f: (A, B) -> R
//): ValidatedNel<E, R> = a.zip(Semigroup.nonEmptyList(), b, f)
//
////implement as many overloads as we need
//fun <E, A, B, C, R> validate(
//    a: ValidatedNel<E, A>,
//    b: ValidatedNel<E, B>,
//    c: ValidatedNel<E, C>,
//    f: (A, B, C) -> R
//): ValidatedNel<E, R> = a.zip(Semigroup.nonEmptyList(), b, c, f)
//
//fun <E, A, B, C, D, R> validate(
//    a: ValidatedNel<E, A>,
//    b: ValidatedNel<E, B>,
//    c: ValidatedNel<E, C>,
//    d: ValidatedNel<E, D>,
//    f: (A, B, C, D) -> R
//): ValidatedNel<E, R> = a.zip(Semigroup.nonEmptyList(), b, c, d, f)
//
//data class EmailRoute(
//    val from: Email,
//    val to: Email,
//    val cc: List<Email> = emptyList(),
//    val bcc: List<Email> = emptyList()
//) {
//    companion object {
//        fun emailRoute(
//            from: String,
//            to: String,
//            cc: List<String>,
//            bcc: List<String>
//        ): Validated<ValidationErrors, EmailRoute> =
//            validate(
//                Email.valueOf(from),
//                Email.valueOf(to),
//                cc.traverseValidated { Email.valueOf(it) },
//                cc.traverseValidated { Email.valueOf(it) }
//            ) { validFrom, validTo, validCc, validBcc ->
//                EmailRoute(validFrom, validTo, validCc, validBcc)
//            }.fold(ValidationErrors::invalid) { emailRoute ->
//                if (emailRoute.cc.isEmpty() && emailRoute.bcc.isEmpty()) {
//                    ValidationError("Both cc and bcc are empty").invalidNel()
//                } else {
//                    emailRoute.valid()
//                }
//            }
//    }
//}
//
//fun <E, A, R> Validated<E, A>.andThen(f: (A) -> Validated<E, R>): Validated<E, R> =
//    fold({ it.invalid() }, f)
//
////TODO operator invoke on validation error companion for lazy messages
////(IDEE: SPY ADAUGAT IN BCC DACA TO E CINEVA)