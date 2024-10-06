package de.auinger.datev.summierer

import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.FileTemplateResolver
import java.io.FileWriter
import java.util.*


class OutputWriter {

    fun write(summary: Summary, templateFile: String, outputFile: String) {
        val templateResolver = FileTemplateResolver()
        templateResolver.setTemplateMode("HTML5")

        val templateEngine = TemplateEngine()
        templateEngine.setTemplateResolver(templateResolver)

        val variables = mapOf(
            "types" to Type.values(),
            "amountsByType" to summary.amountsByType(),
            "summaryItems" to summary.summaryItems().toMutableList().sortedBy { it.datum }
        )
        val context = Context(Locale.getDefault(), variables)
        val out = FileWriter(outputFile)
        templateEngine.process(templateFile, context, out)
    }
}