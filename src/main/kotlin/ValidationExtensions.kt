import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Tuple4
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.invalid
import arrow.core.nel
import arrow.core.zip
import arrow.typeclasses.Semigroup

fun <E, A, B, R> validate(
    a: ValidatedNel<E, A>,
    b: ValidatedNel<E, B>,
    f: (A, B) -> R
): ValidatedNel<E, R> = a.zip(Semigroup.nonEmptyList(), b, f)

fun <E, A, B> validate(
    a: ValidatedNel<E, A>,
    b: ValidatedNel<E, B>
): ValidatedNel<E, Pair<A, B>> = a.zip(Semigroup.nonEmptyList(), b) { validA, validB ->
    validA to validB
}

fun <E, A, B, C, R> validate(
    a: ValidatedNel<E, A>,
    b: ValidatedNel<E, B>,
    c: ValidatedNel<E, C>,
    f: (A, B, C) -> R
): ValidatedNel<E, R> = a.zip(Semigroup.nonEmptyList(), b, c, f)

fun <E, A, B, C> validate(
    a: ValidatedNel<E, A>,
    b: ValidatedNel<E, B>,
    c: ValidatedNel<E, C>
): ValidatedNel<E, Triple<A, B, C>> = a.zip(Semigroup.nonEmptyList(), b, c) { validA, validB, validC ->
    Triple(validA, validB, validC)
}

fun <E, A, B, C, D, R> validate(
    a: ValidatedNel<E, A>,
    b: ValidatedNel<E, B>,
    c: ValidatedNel<E, C>,
    d: ValidatedNel<E, D>,
    f: (A, B, C, D) -> R
): ValidatedNel<E, R> = a.zip(Semigroup.nonEmptyList(), b, c, d, f)

fun <E, A, B, C, D> validate(
    a: ValidatedNel<E, A>,
    b: ValidatedNel<E, B>,
    c: ValidatedNel<E, C>,
    d: ValidatedNel<E, D>
): ValidatedNel<E, Tuple4<A, B, C, D>> = a.zip(Semigroup.nonEmptyList(), b, c, d) { validA, validB, validC, validD ->
    Tuple4(validA, validB, validC, validD)
}

fun <E, A, R> Validated<E, A>.andThen(f: (A) -> Validated<E, R>): Validated<E, R> =
    fold({ it.invalid() }, f)

fun <A, B> Either<A, B>.leftNel(): Either<NonEmptyList<A>, B> = mapLeft { it.nel() }
