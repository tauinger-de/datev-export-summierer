package de.auinger.datev.summierer

import java.math.BigDecimal
import java.time.LocalDate

class SummaryItem(
    val datum: LocalDate,
    val belegNr: String,
    val buchungsDetail: String
) {
    val betraege = mutableMapOf<Type, BigDecimal>()

    fun add(betrag: BigDecimal, type: Type) {
        val betragVorher = betraege.computeIfAbsent(type) { BigDecimal.ZERO }
        betraege[type] = betragVorher.plus(betrag)
    }


    val overallAmount: BigDecimal
        get() = betraege.values.sumByBigDecimal { it }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SummaryItem

        if (datum != other.datum) return false
        if (belegNr != other.belegNr) return false
        if (buchungsDetail != other.buchungsDetail) return false

        return true
    }


    override fun hashCode(): Int {
        var result = datum.hashCode()
        result = 31 * result + belegNr.hashCode()
        return result
    }

}


typealias ExportEntryPredicate = (ExportEntry) -> Boolean

enum class Type(
    val label: String,
    private vararg val predicates: ExportEntryPredicate = emptyArray()
) {
    ERLOES_NETTO("Erlöse"),
    ERLOES_UMST("UmSt"),
    AUSGABE_ABZUGSFAEHIG("Ausgaben"),
    AUSGABE_NICHT_ABZUGSFAEHIG("Ausgaben nicht abz.f."),
    INVESTITION("Investitionen"), // nicht abzugsfaehig
    VORSTEUER("Vorsteuer"), // gezahlte UmSt auf Ausgaben
    PRIVATEINLAGE("Privateinlage"),
    PRIVATENTNAHME("Privatentnahme"),
    UMSATZSTEUER("gez. UmSt."),
    KRANKENKASSE("KK-Beiträge",
        keywordPredicate(listOf("krankenkasse", "mkk", "Beitrag Vormonat")),
        regexpPredicate(""".*Beitrag \d\d.\d\d.20\d\d - \d\d.\d\d.20\d\d.*""")
    ),
    RENTE("RV-Beiträge",
        keywordPredicate(listOf("rentenversicherung", "rente"))
    ),
    ARBEITSLOSENVERSICHERUNG("AV-Beiträge"), // Sonderausgabe
    EINKOMMENSTEUER_VORAUSZAHLUNG("EinkSt-Vorausz.",
        keywordPredicate(listOf("steuervorauszahlung", "einkommensteuer", "EINK.ST", "Rücklage", "Steuerrückstellung"))
    ),
    KIRCHENSTEUER_VORAUSZAHLUNG("KirchenSt-Vorausz.",
        keywordPredicate(listOf("kirchensteuer", "KIRCHENEINKOMMENST"))
    ),
    ABSCHREIBUNG("Abschreibung"), // voll abzugsfähig
    ALTERSVORSORGE("private AV");

    fun matches(entry: ExportEntry): Boolean {
        return predicates.any { it.invoke(entry) }
    }
}

private fun keywordPredicate(keywords: List<String>): (ExportEntry) -> Boolean {
    return { entry: ExportEntry ->
        keywords.any { entry.buchungsDetail.contains(it, ignoreCase = true) }
    }
}

private fun regexpPredicate(regexp: String): (ExportEntry) -> Boolean {
    return { entry: ExportEntry ->
        regexp.toRegex().containsMatchIn(entry.buchungsDetail)
    }
}