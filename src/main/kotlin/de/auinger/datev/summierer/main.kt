package de.auinger.datev.summierer

import java.util.*

fun main(args: Array<String>) {
    Locale.setDefault(Locale.GERMANY)
    if (args.isEmpty()) {
        println("USAGE:")
        println("Provide path to exported DATEV-file as first program argument and the month as second")
    } else {
        val summarizer = Summarizer(
            exportFilePath = args[0],
            month = if (args.size > 1) args[1].toInt() else null
        )
        summarizer.run()
    }
}
