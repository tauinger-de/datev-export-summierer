package de.auinger.datev.summierer

import java.math.BigDecimal

data class ExportEntry(
        val umsatz: BigDecimal,
        val sollHaben: SollHaben,
        val gegenkonto: Int,
        val monat: Int
) {

    val umsatzMitVorzeichen: BigDecimal
        get() = if (sollHaben == SollHaben.S) umsatz else umsatz.negate()
}


enum class SollHaben { S, H }

