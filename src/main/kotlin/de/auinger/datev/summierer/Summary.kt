package de.auinger.datev.summierer

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class Summary {

    fun add(entry: ExportEntry) {
        val summaryItem = getOrCreateSummaryItem(entry = entry)

        when (entry.gegenkonto) {
            400 -> {
                when (entry.sollHaben) {
                    SollHaben.H -> summaryItem.add(betrag = entry.umsatz, type = Type.INVESTITION_ZUR_ABSCHREIBUNG)
                    SollHaben.S -> summaryItem.add(betrag = entry.umsatz, type = Type.ABSCHREIBUNG)
                }
            }
            1588, in 1571..1579 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.VORSTEUER)
            }
            1780 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.UMST_VORAUSZAHLUNG)
            }
            1800 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.PRIVATENTNAHMEN)
            }
            1890 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.PRIVATEINLAGEN)
            }
            in 4000..4999 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.AUSGABE_ABZUGSFAEHIG)
            }
            8400, 8790 -> {
                val umsatzMitVorzeichen = entry.umsatzMitVorzeichen
                val netto = umsatzMitVorzeichen.divide(BigDecimal("1.19"), 2, RoundingMode.HALF_UP)
                summaryItem.add(betrag = netto, type = Type.ERLOES_NETTO)
                val umSt = umsatzMitVorzeichen.minus(netto)
                summaryItem.add(betrag = umSt, type = Type.ERLOES_UMST)
            }
            else -> throw IllegalArgumentException(entry.gegenkonto.toString())
        }

    }

    private val summaryItems = mutableMapOf<SummaryItem, SummaryItem>()

    private fun getOrCreateSummaryItem(entry: ExportEntry): SummaryItem {
        val summaryItemKey = SummaryItem(
                datum = LocalDate.of(2020, entry.monat, 1),
                belegInfo = "TODO",
                belegNr = "TODO")
        return summaryItems.computeIfAbsent(summaryItemKey) { it }
    }


    private fun amountsByType(): Map<Type, BigDecimal> {
        return summaryItems.values
                .flatMap { it.betraege.entries }
                .groupBy({ it.key }, { it.value })
                .map { it.key to it.value.sumByBigDecimal { it } }
                .toMap()
    }


    override fun toString(): String {
        return amountsByType().toString()
    }

    /*
    override fun toString(): String {
        val format = NumberFormat.getCurrencyInstance(Locale.GERMANY)
        return "erloeseBrutto:\t" + format.format(erloeseBrutto.toDouble()) +
                "\nerloeseNetto:\t" + format.format(erloeseNetto.toDouble()) +
                "\nerloeseUmSt:\t" + format.format(erloeseUmSt.toDouble()) +
                "\nprivatEinlagen:\t" + format.format(privatEinlagen.toDouble()) +
                "\nprivatEntnahmen:\t" + format.format(privatEntnahmen.toDouble()) +
                "\nausgabenNetto:\t" + format.format(ausgabenNetto.toDouble()) +
                "\nabziehbareVorsteuer:\t" + format.format(abziehbareVorsteuer.toDouble()) +
                "\nnichtAbsetzbareInvestitionen:\t" + format.format(nichtAbsetzbareInvestitionen.toDouble()) +
                "\nabsetzbareAbschreibung:\t" + format.format(absetzbareAbschreibung.toDouble()) +
                "\numStVorauszahlung:\t" + format.format(umStVorauszahlung.toDouble())
    }
    */
}
