package br.edu.utfpr.appcontatos.data

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Contact(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val isFavorite: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val birthDate: LocalDate = LocalDate.now(),
    val type: ContactTypeEnum = ContactTypeEnum.PERSONAL,
    val assetValue: BigDecimal = BigDecimal.ZERO
) {
    val fullName get(): String = "$firstName $lastName".trim()
}

fun List<Contact>.groupByInitial(): Map<String, List<Contact>> = sortedBy { it.fullName }
    .groupBy { it.fullName.take(1) }

fun LocalDate.format(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return format(formatter)
}

enum class ContactTypeEnum {
    PERSONAL,
    PROFESSIONAL
}