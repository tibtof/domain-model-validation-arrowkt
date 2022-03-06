package domain

fun interface AllowedSenders {
    operator fun contains(email: Email): Boolean
}