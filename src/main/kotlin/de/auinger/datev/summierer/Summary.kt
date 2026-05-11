package de.auinger.datev.summierer

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class Summary {

    private val FACTOR_19_PERCENT = BigDecimal("1.19")
    private val FACTOR_7_PERCENT = BigDecimal("1.07")
    private val FACTOR_0_PERCENT = BigDecimal("1.00")

    fun add(entry: ExportEntry) {
        // ignore items with 0.00 amount so we don't have to deal with weird konto/gegenkonto combinations that don't actually change anything
        if (BigDecimal.ZERO.compareTo(entry.umsatz) == 0) return

        // get summary item to update
        val summaryItem = getOrCreateSummaryItem(entry = entry)

//        if (entry.konto !in listOf(1800,1890)) {
//            println("Buchung auf Konto ${entry.konto}")
//        }

        when (entry.konto) {
            400 -> {
                when (entry.sollHaben) {
                    SollHaben.H -> summaryItem.add(betrag = entry.umsatz, type = Type.INVESTITION)
                    SollHaben.S -> summaryItem.add(betrag = entry.umsatz, type = Type.ABSCHREIBUNG)
                }
            }

            1588, in 1571..1579 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.VORSTEUER)
            }

            1780, 1787, 1790 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.UMSATZSTEUER)
            }

            1791 -> {
                summaryItem.add(betrag = entry.umsatzAlsPositiveAusgabe, type = Type.ERLOES_UMST)
            }

            1800 -> {
                when {
                    // todo complete refactoring to Type predicate and include Gegenkonto somehow
                    Type.RENTE.matches(entry) -> {
                        summaryItem.add(betrag = entry.umsatz, type = Type.RENTE)
                    }

                    Type.KRANKENKASSE.matches(entry) -> {
                        summaryItem.add(betrag = entry.umsatz, type = Type.KRANKENKASSE)
                    }

                    Type.EINKOMMENSTEUER_VORAUSZAHLUNG.matches(entry) -> {
                        summaryItem.add(betrag = entry.umsatz, type = Type.EINKOMMENSTEUER_VORAUSZAHLUNG)
                    }

                    Type.KIRCHENSTEUER_VORAUSZAHLUNG.matches(entry) -> {
                        summaryItem.add(betrag = entry.umsatz, type = Type.KIRCHENSTEUER_VORAUSZAHLUNG)
                    }

                    isAltersvorsorge(entry = entry) -> {
                        summaryItem.add(betrag = entry.umsatz, type = Type.ALTERSVORSORGE)
                    }

                    else -> {
                        summaryItem.add(betrag = entry.umsatz, type = Type.PRIVATENTNAHME)
                    }
                }
            }

            1890, 2650 -> {
                summaryItem.add(betrag = entry.umsatz, type = Type.PRIVATEINLAGE)
            }

            4654 -> {
                summaryItem.add(betrag = entry.umsatzAlsPositiveAusgabe, type = Type.AUSGABE_NICHT_ABZUGSFAEHIG)
            }

            480, 3123, 3830, in 4000..4999 -> {
                // ignore GWG-Abschreibungsbuchungen at end of year since we included their value already via 480 gegenkonto
                if (entry.konto != 4855) {
                    summaryItem.add(betrag = entry.umsatz, type = Type.AUSGABE_ABZUGSFAEHIG)
                }
            }

            8200, 8400, 8790 -> {
                val (tax, net) = calcTaxAndNetAmount(entry)
                summaryItem.add(betrag = tax, type = Type.ERLOES_UMST)
                summaryItem.add(betrag = net, type = Type.ERLOES_NETTO)
            }

            else -> {
                println("WARN :: No handling for Konto ${entry.konto} of $entry")
            }
        }
    }

    private fun calcTaxAndNetAmount(entry: ExportEntry): Pair<BigDecimal, BigDecimal> {
        val umsatzMitVorzeichen = entry.umsatz
        val factor = when (entry.konto) {
            8200 -> FACTOR_0_PERCENT
            8400, 8790 -> FACTOR_19_PERCENT
            else -> throw IllegalArgumentException("Unexpected konto ${entry.konto}")
        }
        val netto = umsatzMitVorzeichen.divide(factor, 2, RoundingMode.HALF_UP)
        val umSt = umsatzMitVorzeichen.minus(netto)
        return Pair(umSt, netto)
    }


    private fun calculateNetAmount(grossAmount: BigDecimal, taxPercent: Int = 19): BigDecimal {
        return grossAmount.divide(BigDecimal("1.$taxPercent"), 2, RoundingMode.HALF_UP);
    }


    private fun isAltersvorsorge(entry: ExportEntry): Boolean {
        val keywords = listOf("Privatentnahme zur Investition", "Altersvorsorge")
        keywords.forEach {
            if (entry.buchungsDetail.contains(it, true)) return true
        }
        return false
    }


    private val summaryItems = mutableMapOf<SummaryItem, SummaryItem>()

    private fun getOrCreateSummaryItem(entry: ExportEntry): SummaryItem {
        val summaryItemKey = SummaryItem(
            datum = LocalDate.of(entry.jahr, entry.monat, entry.tag),
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
