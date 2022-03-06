package cli

import arrow.core.computations.either
import arrow.core.getOrHandle
import arrow.core.handleErrorWith
import arrow.core.left
import arrow.core.right
import config.appConfig
import domain.AllowedSenders
import domain.ApplicationErrors
import domain.EmailRoute.Companion.validated
import domain.InterruptedError
import domain.ReceiveEmailConsents
import leftNel
import log


suspend fun main() = either<ApplicationErrors, Unit> {
    val (allowedSenders, receiverEmailConsent) = appConfig().bind()

    with(ApplicationContext(allowedSenders, receiverEmailConsent)) {
        while (true) {
            runProgram().bind()
        }
    }
}.getOrHandle { errors ->
    errors.log()
}

private suspend fun ApplicationContext.runProgram() = either<ApplicationErrors, Unit> {
    val (from, to, cc, bcc) = readInput().leftNel().bind()
    val emailRoute = validated(from, to, cc, bcc).bind()

    println("Sending email to $emailRoute")
}.handleErrorWith { errors ->
    if (InterruptedError in errors) {
        errors.left()
    } else {
        errors.log()
        Unit.right()
    }
}

class ApplicationContext(
    private val allowedSenders: AllowedSenders,
    private val receiveEmailConsents: ReceiveEmailConsents
) : AllowedSenders by allowedSenders, ReceiveEmailConsents by receiveEmailConsents
