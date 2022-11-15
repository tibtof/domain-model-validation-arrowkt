package cli

import arrow.core.Either
import arrow.core.Tuple4
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import asList
import domain.ApplicationError
import domain.InterruptedError
import domain.RuntimeError


suspend fun readInput():
        Either<ApplicationError, Tuple4<String, String, List<String>, List<String>>> =
    either {
        Tuple4(
            readString("from").bind(),
            readString("to").bind(),
            readString("cc").bind().asList(),
            readString("bcc").bind().asList()
        )
    }

private fun readString(message: String): Either<ApplicationError, String> = try {
    println("$message (q to exit)?")
    val s = readln().trim()
    if (s.equals("q", ignoreCase = true)) InterruptedError.left()
    else s.right()
} catch (e: Exception) {
    RuntimeError(e.message).left()
}
