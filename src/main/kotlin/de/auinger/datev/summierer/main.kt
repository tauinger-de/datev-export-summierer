package de.auinger.datev.summierer

fun main() {
    val summarizer = Summarizer(exportFilePath = "./EXTF_dummy-export.csv")
    summarizer.run()
}
