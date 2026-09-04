package com.contactify.logic

import java.math.BigInteger

fun applyPhoneMask(rawDigits: String, mask: String): String {
    val out = StringBuilder()
    var index = 0
    for (ch in mask) {
        if (index >= rawDigits.length) break
        if (ch == '0') {
            out.append(rawDigits[index++])
        } else {
            out.append(ch)
        }
    }
    return out.toString()
}

fun incrementPhone(raw: String, amount: Long): String {
    if (raw.isBlank()) return ""
    return (BigInteger(raw) + BigInteger.valueOf(amount))
        .toString()
        .padStart(raw.length, '0')
}

fun generateMask(length: Int): String = when (length) {
    11 -> "000 0000 0000"
    10 -> "000 000 0000"
    9 -> "000 000 000"
    8 -> "0000 0000"
    7 -> "000 0000"
    6 -> "000 000"
    else -> "0".repeat(length).chunked(1).joinToString(" ")
}
