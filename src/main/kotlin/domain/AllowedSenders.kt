package domain

fun interface AllowedSenders {
    fun isSenderAllowed(email: Email): Boolean
}