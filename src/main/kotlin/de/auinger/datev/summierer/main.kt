package de.auinger.datev.summierer

fun main(args:Array<String>) {
    if (args.isEmpty()) {
        println("Provider input export file as program argument 0 and month as argument 1")
    }
    else {
        val summarizer = Summarizer(exportFilePath = args[0], month = args[1].toInt())
        summarizer.run()
    }
}
