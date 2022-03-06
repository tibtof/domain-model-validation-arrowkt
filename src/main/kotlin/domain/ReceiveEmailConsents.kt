package domain

fun interface ReceiveEmailConsents {
    operator fun contains(email: Email): Boolean
}
