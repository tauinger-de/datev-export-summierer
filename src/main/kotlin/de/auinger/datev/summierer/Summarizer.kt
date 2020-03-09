package de.auinger.datev.summierer

import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths


class Summarizer(private val exportFilePath:String) : Runnable {

    override fun run() {
        val entries = readEntries2()

        val summary = Summary()
        entries.forEach { summary.add(it) }

        println(summary)
    }

    private fun readEntries(): List<ExportEntry> {
        val lines = Files.readAllLines(Paths.get(exportFilePath), Charset.forName("ISO-8859-1"))

        // skip 2 header lines
        lines.removeAt(0)
        lines.removeAt(0)

        // parse to entries
        val converter = ExportEntryConverter()
        return lines.map {  converter.convert(it) }.toList()
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
        return lines.map {  converter.convert(it) }.toList()
    }
}

/*

           val mapper = CsvMapper()
            val schema = mapper.schemaFor(TriviaQuestionImportDto::class.java)
                    .withHeader()
                    .withStrictHeaders(true)
                    .withColumnSeparator('\t')
            return mapper.readerFor(TriviaQuestionImportDto::class.java)
                    .with(schema)
                    .readValues<TriviaQuestionImportDto>(csv)
                    .readAll()

 */
