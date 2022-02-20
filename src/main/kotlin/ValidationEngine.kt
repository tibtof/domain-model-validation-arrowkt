import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.invalid
import arrow.core.zip
import arrow.typeclasses.Semigroup

fun <E, A, B, R> validate(
    a: ValidatedNel<E, A>,
    b: ValidatedNel<E, B>,
    f: (A, B) -> R
): ValidatedNel<E, R> = a.zip(Semigroup.nonEmptyList(), b, f)

fun <E, A, B, C, R> validate(
    a: ValidatedNel<E, A>,
    b: ValidatedNel<E, B>,
    c: ValidatedNel<E, C>,
    f: (A, B, C) -> R
): ValidatedNel<E, R> = a.zip(Semigroup.nonEmptyList(), b, c, f)

fun <E, A, B, C, D, R> validate(
    a: ValidatedNel<E, A>,
    b: ValidatedNel<E, B>,
    c: ValidatedNel<E, C>,
    d: ValidatedNel<E, D>,
    f: (A, B, C, D) -> R
): ValidatedNel<E, R> = a.zip(Semigroup.nonEmptyList(), b, c, d, f)

fun <E, A, R> Validated<E, A>.andThen(f: (A) -> Validated<E, R>): Validated<E, R> =
    fold({ it.invalid() }, f)
