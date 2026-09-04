package com.contactify.logic

import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter

fun writeVcf(
    output: OutputStream,
    name: String,
    countryCode: String,
    phoneRaw: String,
    count: Long,
    format: String
) {
    BufferedWriter(OutputStreamWriter(output, Charsets.UTF_8), 64 * 1024).use { writer ->
        for (i in 0 until count) {
            val number = incrementPhone(phoneRaw, i)
            val displayName = format
                .replace("{name}", name)
                .replace("1", (i + 1).toString())
            val fullPhone = "$countryCode$number"

            writer.appendLine("BEGIN:VCARD")
            writer.appendLine("VERSION:3.0")
            writer.appendLine("FN:${escapeVCard(displayName)}")
            writer.appendLine("TEL;TYPE=CELL:$fullPhone")
            writer.appendLine("END:VCARD")
        }
    }
}

private fun escapeVCard(value: String): String =
    value.replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace(";", "\\;")
        .replace(",", "\\,")
