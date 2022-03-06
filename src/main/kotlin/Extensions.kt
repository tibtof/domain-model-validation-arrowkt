import domain.ApplicationErrors

fun String.asList(delimiter: String = ",") = split(delimiter)
    .map { it.trim() }
    .filter { it.isNotEmpty() }

fun ApplicationErrors.log() = forEach {
    println(it)
}
