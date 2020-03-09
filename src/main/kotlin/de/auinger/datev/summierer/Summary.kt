package de.auinger.datev.summierer

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

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
}
