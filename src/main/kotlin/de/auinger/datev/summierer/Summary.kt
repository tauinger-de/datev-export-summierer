package de.auinger.datev.summierer

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class Summary {

    fun add(entry: ExportEntry) {
        val summaryItem = getOrCreateSummaryItem(entry = entry)

//        if (entry.konto !in listOf(1800,1890)) {
//            println("Buchung auf Konto ${entry.konto}")
//        }

        when (entry.gegenkonto) {
            400 -> {
                when (entry.sollHaben) {
                    SollHaben.H -> summaryItem.add(betrag = entry.umsatz, type = Type.INVESTITION)
                    SollHaben.S -> summaryItem.add(betrag = entry.umsatz, type = Type.ABSCHREIBUNG)
                }
            }
            1588, in 1571..1579 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.VORSTEUER)
            }
            1780, 1790 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.UMSATZSTEUER)
            }
            1800 -> {
                when {
                    isSoderausgabeKrankenkasse(entry = entry) -> {
                        summaryItem.add(betrag = entry.umsatz, type = Type.KRANKENKASSE)
                    }
                    isSoderausgabeRente(entry = entry) -> {
                        summaryItem.add(betrag = entry.umsatz, type = Type.RENTE)
                    }
                    isPrivatsteuer(entry = entry) -> {
                        summaryItem.add(betrag = entry.umsatz, type = Type.PRIVATSTEUER)
                    }
                    else -> {
                        summaryItem.add(betrag = entry.umsatz, type = Type.PRIVATENTNAHME)
                    }
                }
            }
            1890 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.PRIVATEINLAGE)
            }
            4654 -> {
                summaryItem.add(betrag = entry.umsatzAlsPositiveAusgabe, type = Type.AUSGABE_NICHT_ABZUGSFAEHIG)
            }
            4674 -> {
                summaryItem.add(betrag = entry.umsatzAlsPositiveAusgabe, type = Type.SPESEN)
            }
            480, in 4000..4999 -> {
                // ignore GWG-Abschreibungsbuchungen at end of year since we included their value already via 480 gegenkonto
                if (entry.konto != 4855) {
                    summaryItem.add(betrag = entry.umsatzAlsPositiveAusgabe, type = Type.AUSGABE_ABZUGSFAEHIG)
                }
            }
            8400, 8790 -> {
                // thx to Corona we have only 16% UmSt for 1.7.2020 - 31.12.2020
                val umsatzMitVorzeichen = entry.umsatzAlsPositiveEinnahme
                val netto = if (entry.jahr == 2020 && entry.monat >= 7) {
                    umsatzMitVorzeichen.divide(BigDecimal("1.16"), 2, RoundingMode.HALF_UP)
                }
                else {
                    umsatzMitVorzeichen.divide(BigDecimal("1.19"), 2, RoundingMode.HALF_UP)
                }
                summaryItem.add(betrag = netto, type = Type.ERLOES_NETTO)
                val umSt = umsatzMitVorzeichen.minus(netto)
                summaryItem.add(betrag = umSt, type = Type.ERLOES_UMST)
            }
            else -> throw IllegalArgumentException("No handling for 'Gegenkonto' of $entry")
        }
    }


    private fun isSoderausgabeKrankenkasse(entry: ExportEntry): Boolean {
        val keywords = listOf("barmer", "DE59ZZZ00000074082")
        keywords.forEach {
            if (entry.buchungsDetail.contains(it, true)) return true
        }
        return false
    }


    private fun isSoderausgabeRente(entry: ExportEntry): Boolean {
        val keywords = listOf("rentenversicherung")
        keywords.forEach {
            if (entry.buchungsDetail.contains(it, true)) return true
        }
        return false
    }


    private fun isPrivatsteuer(entry: ExportEntry): Boolean {
        val keywords = listOf("steuervorauszahlung", "einkommensteuer", "kirchensteuer")
        keywords.forEach {
            if (entry.buchungsDetail.contains(it, true)) return true
        }
        return false
    }


    private val summaryItems = mutableMapOf<SummaryItem, SummaryItem>()

    private fun getOrCreateSummaryItem(entry: ExportEntry): SummaryItem {
        val summaryItemKey = SummaryItem(
            datum = LocalDate.of(2020, entry.monat, entry.tag),
            buchungsDetail = entry.buchungsDetail,
            belegNr = entry.belegfeld1
        )
        return summaryItems.computeIfAbsent(summaryItemKey) { it }
    }


    fun amountsByType(): Map<Type, BigDecimal> {
        return summaryItems.values
            .flatMap { it.betraege.entries }
            .groupBy({ it.key }, { it.value })
            .map { it.key to it.value.sumByBigDecimal { it } }
            .toMap()
    }


    fun summaryItems(): Collection<SummaryItem> {
        return this.summaryItems.values
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
