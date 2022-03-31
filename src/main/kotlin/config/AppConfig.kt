package config

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.traverseValidated
import asList
import domain.AllowedSenders
import domain.ApplicationErrors
import domain.Email
import domain.ReceiveEmailConsents
import domain.RuntimeError
import leftNel
import validate
import java.util.Properties


suspend fun appConfig(): Either<ApplicationErrors, Pair<AllowedSenders, ReceiveEmailConsents>> =
    either {
        val (allowedSenderProperties, receiveEmailConsentsProperties) = readProperties().leftNel().bind()

        val (validatedAllowedSenders, validatedConsents) = validate(
            allowedSenderProperties.traverseValidated { Email.valueOf(it) },
            receiveEmailConsentsProperties.traverseValidated { Email.valueOf(it) }
        ).bind()

        AllowedSenders { validatedAllowedSenders.contains(it) } to
                ReceiveEmailConsents { validatedConsents.contains(it) }
    }

private fun <T : Any> T.readProperties() =
    Either.catch {
        this::class.java.getResourceAsStream(
            "/application.properties"
        ).use { inputStream ->
            val properties = Properties().apply { load(inputStream) }

            val allowedSendersProperties = properties["allowed-senders"].toString().asList()

            val receiveEmailConsentsProperties = properties["receive-email-consents"].toString().asList()

            allowedSendersProperties to receiveEmailConsentsProperties
        }
    }.mapLeft {
        RuntimeError(it.message)
    }

