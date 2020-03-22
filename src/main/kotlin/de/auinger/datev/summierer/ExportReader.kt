package de.auinger.datev.summierer

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import java.io.*
import java.nio.charset.Charset

class ExportReader {

    fun readEntries(contentString: String): List<ExportEntry> {
        val reader = StringReader(contentString)
        return internalReadEntries(reader = reader, dropHeaders = false)
    }


    fun readEntries(contentFile: File): List<ExportEntry> {
        val reader = InputStreamReader(FileInputStream(contentFile), Charset.forName("ISO-8859-1"))
        return internalReadEntries(reader = reader)
    }


    private fun internalReadEntries(reader: Reader, dropHeaders:Boolean=true): List<ExportEntry> {
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
        return lines.map { converter.convert(it) }.filter { !it.storniert }.toList()
    }

}