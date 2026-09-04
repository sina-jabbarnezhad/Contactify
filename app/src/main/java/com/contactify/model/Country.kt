package com.contactify.model

data class Country(
    val name: String,
    val code: String,
    val length: Int,
    val emoji: String
) {
    val mask: String
        get() = when (length) {
            11 -> "000 0000 0000"
            10 -> "000 000 0000"
            9 -> "000 000 000"
            8 -> "0000 0000"
            7 -> "000 0000"
            6 -> "000 000"
            else -> "0".repeat(length).chunked(1).joinToString(" ")
        }
}
