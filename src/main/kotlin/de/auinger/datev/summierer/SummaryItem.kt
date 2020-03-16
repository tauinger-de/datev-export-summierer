package de.auinger.datev.summierer

import java.math.BigDecimal
import java.time.LocalDate

class SummaryItem(
        val datum: LocalDate,
        val belegNr: String,
        val belegInfo: String
) {
    val betraege = mutableMapOf<Type, BigDecimal>()

    fun add(betrag:BigDecimal, type: Type) {
        val betragVorher = betraege.computeIfAbsent(type) { BigDecimal.ZERO }
        betraege[type] = betragVorher.plus(betrag)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SummaryItem

        if (datum != other.datum) return false
        if (belegNr != other.belegNr) return false
        if (belegInfo != other.belegInfo) return false

        return true
    }


    override fun hashCode(): Int {
        var result = datum.hashCode()
        result = 31 * result + belegNr.hashCode()
        result = 31 * result + belegInfo.hashCode()
        return result
    }

}


enum class Type {

    ERLOES_NETTO,
    ERLOES_UMST,
    ABSCHREIBUNG,
    AUSGABE_ABZUGSFAEHIG,
    INVESTITION_ZUR_ABSCHREIBUNG,
    VORSTEUER,
    PRIVATENTNAHMEN,
    PRIVATEINLAGEN,
    UMST_VORAUSZAHLUNG

}