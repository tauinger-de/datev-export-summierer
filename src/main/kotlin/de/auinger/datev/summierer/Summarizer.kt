package de.auinger.datev.summierer

import java.io.File
import java.math.BigDecimal
import java.text.NumberFormat


class Summarizer(
        private val exportFilePath: String,
        private val month: Int
) : Runnable {

    override fun run() {
        // read all
        val exportEntries = ExportReader().readEntries(File(exportFilePath)).toMutableList()

        // kick stornos - for each 'reversing' entry should exist a 'reversed' counterpart
        val reversingEntries = exportEntries.filter { it.isReversal }.toList()
        exportEntries.removeAll(reversingEntries)

        val reversedEntries = reversingEntries
                .mapNotNull { reversingEntry ->
                    exportEntries.firstOrNull { exportEntry ->
                        reversingEntry.belegfeld1 == exportEntry.belegfeld1 &&
                        reversingEntry.umsatz == exportEntry.umsatz
                    }
                }
                .toList()
        exportEntries.removeAll(reversedEntries)

        // build summary
        val summary = Summary()
        exportEntries
                .filter { it.monat == month }
                .forEach { summary.add(it) }

        val amountsByType = summary.amountsByType()
        val numberFormat = NumberFormat.getInstance()
        Type.values().forEach {
            val amount = amountsByType[it] ?: BigDecimal.ZERO
            println(numberFormat.format(amount))
        }

        OutputWriter().write(
                summary = summary,
                templateFile = "report-template.html",
                outputFile = "report.html"
        )
    }

}
