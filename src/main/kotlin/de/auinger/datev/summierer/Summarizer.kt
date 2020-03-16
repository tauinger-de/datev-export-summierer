package de.auinger.datev.summierer

import java.io.File


class Summarizer(
        private val exportFilePath: String,
        private val month: Int
) : Runnable {

    override fun run() {
        val entries = ExportReader().readEntries(File(exportFilePath))

        val summary = Summary()
        entries
                .filter { it.monat == month }
                .forEach { summary.add(it) }

        println(summary)
    }

}
