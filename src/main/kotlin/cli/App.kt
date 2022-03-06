import arrow.core.computations.either
import arrow.core.getOrHandle
import arrow.core.handleErrorWith
import arrow.core.left
import arrow.core.right
import cli.readInput
import config.appConfig
import domain.ApplicationErrors
import domain.CreateValidatedEmailRoute
import domain.EmailRoute
import domain.InterruptedError

suspend fun main() = either<ApplicationErrors, Unit> {
    val (allowedSenders, receiverEmailConsent) = appConfig().bind()

    val createValidatedEmailRoute = EmailRoute.factoryWithContext(allowedSenders, receiverEmailConsent)
    while (true) {
        runProgram(createValidatedEmailRoute).bind()
    }
}.getOrHandle { errors ->
    errors.log()
}

private suspend fun runProgram(createValidatedEmailRoute: CreateValidatedEmailRoute) = either<ApplicationErrors, Unit> {
    val (from, to, cc, bcc) = readInput().leftNel().bind()
    val emailRoute = createValidatedEmailRoute(from, to, cc, bcc).bind()

    println("Sending email to $emailRoute")
}.handleErrorWith { errors ->
    if (InterruptedError in errors) {
        errors.left()
    } else {
        errors.log()
        Unit.right()
    }
}
