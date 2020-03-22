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


    fun convert(parts: List<String>): ExportEntry {
        try {
            return ExportEntry(
                    umsatz = parseAmount(parts[0]),
                    sollHaben = SollHaben.valueOf(parts[1]),
                    gegenkonto = parts[7].toInt(),
                    tag = dateMonthRegexp.matchEntire(parts[9])?.groupValues?.get(1)?.toInt() ?: -1,
                    monat = dateMonthRegexp.matchEntire(parts[9])?.groupValues?.get(2)?.toInt() ?: -1,
                    belegfeld1 = parts[10],
                    belegfeld2 = parts[11],
                    buchungsText = parts[13],
                    buchungsDetail = parts[23]
            )
        } catch (iae: IllegalArgumentException) {
            throw ParseException(parts.joinToString(";"), iae)
        }
    }


    private fun parseAmount(str: String): BigDecimal {
        return bigDecimalParser.parseObject(str) as BigDecimal
    }
}
