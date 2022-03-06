package domain

fun interface ReceiveEmailConsents {
    fun consentsReceivingEmails(email: Email): Boolean
}
