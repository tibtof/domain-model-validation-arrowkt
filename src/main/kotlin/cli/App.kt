package cli

import arrow.core.continuations.either
import arrow.core.getOrHandle
import arrow.core.handleErrorWith
import arrow.core.left
import arrow.core.right
import config.appConfig
import domain.AllowedSenders
import domain.EmailRoute
import domain.InterruptedError
import domain.ReceiveEmailConsents
import leftNel
import log

suspend fun main() = either {
    val (allowedSenders, receiverEmailConsent) = appConfig().bind()

    with(allowedSenders) {
        with(receiverEmailConsent) {
            while (true) {
                runProgram().bind()
            }
        }
    }
}.getOrHandle { errors ->
    errors.log()
}

context(AllowedSenders, ReceiveEmailConsents)
        private suspend fun runProgram() = either {
    val (from, to, cc, bcc) = readInput().leftNel().bind()
    val emailRoute = EmailRoute.validated(from, to, cc, bcc).bind()

    println("Sending email to $emailRoute")
}.handleErrorWith { errors ->
    if (InterruptedError in errors) {
        errors.left()
    } else {
        errors.log()
        Unit.right()
    }
}
