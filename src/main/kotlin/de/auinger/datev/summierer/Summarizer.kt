package de.auinger.datev.summierer

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

class Summarizer(private val exportFilePath:String) {

    init {
        readFile()
    }

    private fun readFile() {
        val lines = Files.readAllLines(Paths.get(exportFilePath), Charset.forName("ISO-8859-1"))
        val dataWithoutHeaders = lines.subList(2, lines.size).joinToString("\\n")

        val mapper = CsvMapper()
        val schema = mapper.schemaFor(ExportEntry::class.java)
                .withColumnSeparator(';')
        mapper.readerWithSchemaFor(ExportEntry::class.java)
                .with(schema)
                .readValue<List<ExportEntry>>(dataWithoutHeaders)
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
