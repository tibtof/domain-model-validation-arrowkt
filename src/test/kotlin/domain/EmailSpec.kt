package domain

import arrow.core.nonEmptyListOf
import io.kotest.assertions.arrow.core.shouldBeInvalid
import io.kotest.assertions.arrow.core.shouldBeValid
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class EmailSpec : FreeSpec({

    "model.Email.valueOf should return Valid" - {
        val validEmail = "test@localhost"

        "$validEmail should be valid" {
            //shouldBeValid can extract the model.Email from Valid<model.Email>
            val email: Email = Email.valueOf(validEmail).shouldBeValid()

            email.value shouldBe validEmail
        }

        "$validEmail should be valid (alternative)" {
            /**
             * We are unable to build an model.Email without having it
             * wrapped in a Valid<model.Email>
             * For testing purposes we can create a function
             * that seems to build directly an model.Email
             */
            fun validEmail(value: String): Email = Email.valueOf(value).shouldBeValid()

            //shouldBeValid can also check both the type and the value
            Email.valueOf(validEmail) shouldBeValid validEmail(validEmail)
        }
    }

    "model.Email.valueOf should return Invalid" - {
        "should invalidate null" {
            //shouldBeInvalid can extracts the ValidationErrors from Invalid<ValidationErrors>
            val errors: ValidationErrors = Email.valueOf(null).shouldBeInvalid()

            errors shouldBe nonEmptyListOf(
                ValidationError("email should not be null")
            )
        }

        val invalidEmail = "not an email"

        "$invalidEmail should be invalid" {
            //shouldBeInvalid can also check both the type and the value
            Email.valueOf(invalidEmail) shouldBeInvalid nonEmptyListOf(
                ValidationError("'$invalidEmail' should be a valid email address")
            )
        }
    }
})
