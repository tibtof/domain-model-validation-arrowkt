import arrow.core.NonEmptyList

data class ValidationError(val message: String)
typealias ValidationErrors = NonEmptyList<ValidationError>
