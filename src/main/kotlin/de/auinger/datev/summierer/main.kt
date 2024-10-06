package de.auinger.datev.summierer

import java.util.*

fun main(args: Array<String>) {
    Locale.setDefault(Locale.GERMANY)
    if (args.isEmpty()) {
        println("Provider input export file as program argument 0 and month as argument 1")
    } else {
        val summarizer = Summarizer(
            exportFilePath = args[0],
            month = if (args.size > 1) args[1].toInt() else null
        )
        summarizer.run()
    }
}
