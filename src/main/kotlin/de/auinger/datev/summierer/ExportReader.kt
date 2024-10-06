package de.auinger.datev.summierer

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import java.io.*
import java.nio.charset.Charset

class ExportReader {

    fun readEntries(contentString: String, jahr: Int): List<ExportEntry> {
        val reader = StringReader(contentString)
        return internalReadEntries(reader = reader, dropHeaders = false, jahr = jahr)
    }


    fun readEntries(contentFile: File): List<ExportEntry> {
        val jahr: Int = """bis_\d\d_\d\d_(\d\d\d\d)_""".toRegex().find(contentFile.name)?.groupValues?.get(1)?.toInt()
            ?: throw IllegalStateException("Couldn't extract year from input file name")
        println("Für Jahr: $jahr")
        val reader = InputStreamReader(FileInputStream(contentFile), Charset.forName("ISO-8859-1"))
        return internalReadEntries(reader = reader, jahr = jahr)
    }


    private fun internalReadEntries(reader: Reader, dropHeaders: Boolean = true, jahr: Int): List<ExportEntry> {
        // read
        val csvMapper = CsvMapper()
        val csvSchema = csvMapper.schema().withColumnSeparator(';')
        csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY)
        val rows = csvMapper.readerFor(List::class.java).with(csvSchema).readValues<List<String>>(reader)
        val lines = rows.readAll().toMutableList()

        // skip 2 header lines
        if (dropHeaders) {
            lines.removeAt(0)
            lines.removeAt(0)
        }

        // parse to entries
        val converter = ExportEntryConverter()
        return lines.map { converter.convert(it, jahr) }.toList()
    }

}