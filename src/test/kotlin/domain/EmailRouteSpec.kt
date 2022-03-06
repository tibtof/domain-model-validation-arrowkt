package domain

import arrow.core.invalid
import arrow.core.invalidNel
import arrow.core.nonEmptyListOf
import io.kotest.assertions.arrow.core.shouldBeValid
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class EmailRouteSpec : FreeSpec({

    val allSendersAllowed = AllowedSenders { true }
    val noSendersAllowed = AllowedSenders { false }
    val allReceiversConsent = ReceiveEmailConsents { true }
    val noReceiversConsent = ReceiveEmailConsents { false }

    "given all senders allowed and all receivers consent" - {
        val createValidatedEmailRoute = EmailRoute.factoryWithContext(allSendersAllowed, allReceiversConsent)

        "should validate when all validation rules pass" {
            val validatedEmailRoute = createValidatedEmailRoute(
                "sender@localhost",
                "receiver@localhost",
                listOf("cc@localhost"),
                emptyList()
            )

            with(validatedEmailRoute.shouldBeValid()) {
                from shouldBe validEmail("sender@localhost")
                to shouldBe validEmail("receiver@localhost")
                cc shouldBe listOf(validEmail("cc@localhost"))
                bcc shouldBe emptyList()
            }
        }

        "should invalidate both empty cc and bcc" {
            val emailRoute = createValidatedEmailRoute(
                "sender@localhost",
                "receiver@localhost",
                emptyList(),
                emptyList()
            )

            emailRoute shouldBe ValidationError("Both cc and bcc are empty").invalidNel()
        }

        "should accumulate errors" {
            val emailRoute = createValidatedEmailRoute(
                "invalid-from",
                "invalid-to",
                listOf("invalid-cc"),
                listOf("invalid-bcc")
            )

            emailRoute shouldBe nonEmptyListOf(
                ValidationError("'invalid-from' should be a valid email address"),
                ValidationError("'invalid-to' should be a valid email address"),
                ValidationError("'invalid-cc' should be a valid email address"),
                ValidationError("'invalid-bcc' should be a valid email address")
            ).invalid()
        }
    }

    "should invalidate when the sender is not allowed" {
        val createValidatedEmailRoute = EmailRoute.factoryWithContext(noSendersAllowed, allReceiversConsent)

        val validatedEmailRoute = createValidatedEmailRoute(
            "sender@localhost",
            "receiver@localhost",
            listOf("cc@localhost"),
            emptyList()
        )

        validatedEmailRoute shouldBe
                ValidationError("'sender@localhost' is not in the list of allowed senders").invalidNel()
    }

    "should invalidate when the receiver does not consent receiving emails" {
        val createValidatedEmailRoute = EmailRoute.factoryWithContext(allSendersAllowed, noReceiversConsent)

        val validatedEmailRoute = createValidatedEmailRoute(
            "sender@localhost",
            "receiver@localhost",
            listOf("cc@localhost"),
            emptyList()
        )

        validatedEmailRoute shouldBe
                ValidationError("'receiver@localhost' does not consent receiving emails").invalidNel()
    }
})

fun validEmail(value: String): Email = Email.valueOf(value).shouldBeValid()
