package de.auinger.datev.summierer

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.*


class ExportEntryConverter {

    companion object {
        private val bigDecimalParser = NumberFormat.getInstance(Locale.GERMAN) as DecimalFormat
        private val dateParser = DateTimeFormatter.ofPattern("ddMM")
        private val dateMonthRegexp = """(\d?\d)(\d\d)""".toRegex()

        init {
            bigDecimalParser.isParseBigDecimal = true
        }
    }

    fun convert(line: String): ExportEntry {
        val parts = line.split(";")
        return convert(parts)
    }


    fun convert(parts: List<String>): ExportEntry {
        try {
            return ExportEntry(
                    umsatz = parseAmount(parts[0]),
                    sollHaben = SollHaben.valueOf(parts[1]),
                    gegenkonto = parts[7].toInt(),
                    monat = dateMonthRegexp.matchEntire(parts[8])?.groupValues?.get(2)?.toInt() ?: -1
            )
        } catch (iae: IllegalArgumentException) {
            throw ParseException(parts.joinToString(";"), iae)
        }
    }

    private fun parseAmount(str: String): BigDecimal {
        return bigDecimalParser.parseObject(str) as BigDecimal
    }
}
