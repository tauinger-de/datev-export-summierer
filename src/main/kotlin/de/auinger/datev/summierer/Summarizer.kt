package de.auinger.datev.summierer

import java.io.File
import java.math.BigDecimal
import java.text.NumberFormat


class Summarizer(
    private val exportFilePath: String,
    private val month: Int?
) : Runnable {

    override fun run() {
        // read all
        val entries = ExportReader().readEntries(File(exportFilePath)).toMutableList()

        // remove storno entries - for each 'reversing' entry should exist a 'reversed' counterpart
        val reversingEntries = entries.filter { it.isReversal }.toList()
        entries.removeAll(reversingEntries)

        val reversedEntries = reversingEntries
            .mapNotNull { reversingEntry ->
                entries.firstOrNull { exportEntry ->
                    reversingEntry.belegfeld1 == exportEntry.belegfeld1 &&
                            reversingEntry.umsatz == exportEntry.umsatz
                }
            }
            .toList()
        entries.removeAll(reversedEntries)

        // build summary
        val summary = Summary()
        entries
            .filter { month == null || it.monat == month }
            .forEach { summary.add(it) }
        val amountsByType = summary.amountsByType()

        // print general summary
        val numberFormat = NumberFormat.getInstance()
        Type.values().forEach {
            val amount = amountsByType[it] ?: BigDecimal.ZERO
            println(numberFormat.format(amount))
        }
        println("-".repeat(40))

        // print cash flow doc-specific summary
        listOf(
            Type.ERLOES_NETTO, null, Type.AUSGABE_ABZUGSFAEHIG, null, Type.KRANKENKASSE, Type.RENTE,
            Type.ARBEITSLOSENVERSICHERUNG, null, Type.EINKOMMENSTEUER_VORAUSZAHLUNG, Type.KIRCHENSTEUER_VORAUSZAHLUNG
        ).forEach {
            if (it == null) {
                println()
            } else {
                val amount = amountsByType[it] ?: BigDecimal.ZERO
                println(numberFormat.format(amount))
            }
        }

        // write report
        OutputWriter().write(
            summary = summary,
            templateFile = "report-template.html",
            outputFile = "report.html"
        )
    }

}
