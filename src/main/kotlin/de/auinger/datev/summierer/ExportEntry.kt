package de.auinger.datev.summierer

import java.math.BigDecimal

data class ExportEntry(
        val umsatz: BigDecimal,
        val sollHaben: SollHaben,
        val gegenkonto: Int,
        val tag: Int,
        val monat: Int,
        val belegfeld1: String,
        val belegfeld2: String,
        val buchungsText: String,
        val buchungsDetail: String
) {

    val umsatzMitVorzeichen: BigDecimal
        get() = if (sollHaben == SollHaben.S) umsatz else umsatz.negate()


    val storniert: Boolean
        get() = belegfeld2 == "Storno"

}


enum class SollHaben { S, H }

