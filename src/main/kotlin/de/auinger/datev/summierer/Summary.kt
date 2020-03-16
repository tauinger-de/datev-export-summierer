package de.auinger.datev.summierer

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.util.*
import de.auinger.datev.summierer.sumByBigDecimal

class Summary {

    var erloeseBrutto: BigDecimal = BigDecimal.ZERO
    var erloeseNetto: BigDecimal = BigDecimal.ZERO
    var erloeseUmSt: BigDecimal = BigDecimal.ZERO

    var privatEinlagen: BigDecimal = BigDecimal.ZERO
    var privatEntnahmen: BigDecimal = BigDecimal.ZERO

    var ausgabenNetto: BigDecimal = BigDecimal.ZERO
    var abziehbareVorsteuer: BigDecimal = BigDecimal.ZERO
    var nichtAbsetzbareInvestitionen: BigDecimal = BigDecimal.ZERO
    var absetzbareAbschreibung: BigDecimal = BigDecimal.ZERO

    var umStVorauszahlung: BigDecimal = BigDecimal.ZERO


    fun add(entry: ExportEntry) {
        val summaryItem = getOrCreateSummaryItem(entry = entry)

        when (entry.gegenkonto) {
            1800 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.PRIVATENTNAHMEN)
            }
            1890 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.PRIVATEINLAGEN)
            }
            in 4000..4999 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.AUSGABE_ABZUGSFAEHIG)
            }
            else -> throw IllegalArgumentException(entry.gegenkonto.toString())
        }

    }

    /*
    fun add(entry: ExportEntry) {
        when (entry.gegenkonto) {
            400 -> {
                when (entry.sollHaben) {
                    SollHaben.H -> nichtAbsetzbareInvestitionen = nichtAbsetzbareInvestitionen.plus(entry.umsatz)
                    SollHaben.S -> absetzbareAbschreibung = absetzbareAbschreibung.plus(entry.umsatz)
                }
            }
            1588, in 1571..1579 -> {
                abziehbareVorsteuer = abziehbareVorsteuer.add(entry.umsatz)
            }
            1780 -> {
                umStVorauszahlung = umStVorauszahlung.add(entry.umsatz)
            }
            1800 -> {
                privatEntnahmen = privatEntnahmen.add(entry.umsatz)
            }
            1890 -> {
                privatEinlagen = privatEinlagen.add(entry.umsatz)
            }
            in 4000..4999 -> {
                ausgabenNetto = ausgabenNetto.plus(entry.umsatz)
            }
            8400, 8790 -> {
                val umsatzMitVorzeichen = entry.umsatzMitVorzeichen
                erloeseBrutto = erloeseBrutto.add(umsatzMitVorzeichen)
                val netto = umsatzMitVorzeichen.divide(BigDecimal("1.19"), 2, BigDecimal.ROUND_HALF_UP)
                erloeseNetto = erloeseNetto.add(netto)
                val umSt = umsatzMitVorzeichen.minus(netto)
                erloeseUmSt = erloeseUmSt.add(umSt)
            }
            else -> throw IllegalArgumentException(entry.gegenkonto.toString())
        }
    }
     */


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
        return amountsByType()
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
