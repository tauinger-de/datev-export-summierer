package de.auinger.datev.summierer

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate


class Summarizer(
        private val exportFilePath: String,
        private val month: Int
) : Runnable {

    override fun run() {
        val entries = readEntries2()

        val summary = Summary()
        entries
                .filter { it.monat == month }
                .forEach { summary.add(it) }

        println(summary)
    }


    private val summaryItems = mutableMapOf<SummaryItem,SummaryItem>()

    private fun getOrCreateSummaryItem(entry: ExportEntry): SummaryItem {
        val summaryItem = SummaryItem(
                datum = LocalDate.of(2020, entry.monat, 1),
                belegInfo = "TODO",
                belegNr = "TODO")
    }


    private fun readEntries(): List<ExportEntry> {
        val lines = Files.readAllLines(Paths.get(exportFilePath), Charset.forName("ISO-8859-1"))

        // skip 2 header lines
        lines.removeAt(0)
        lines.removeAt(0)

        // parse to entries
        val converter = ExportEntryConverter()
        return lines.map { converter.convert(it) }.toList()
    }


    private fun readEntries2(): List<ExportEntry> {
        // read
        val reader = InputStreamReader(FileInputStream(exportFilePath), Charset.forName("ISO-8859-1"))
        val csvMapper = CsvMapper()
        val csvSchema = csvMapper.schema().withColumnSeparator(';')
        csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY)
        val rows = csvMapper.readerFor(List::class.java).with(csvSchema).readValues<List<String>>(reader)
        val lines = rows.readAll().toMutableList()

        // skip 2 header lines
        lines.removeAt(0)
        lines.removeAt(0)

        // parse to entries
        val converter = ExportEntryConverter()
        return lines.map { converter.convert(it) }.toList()
    }
}
