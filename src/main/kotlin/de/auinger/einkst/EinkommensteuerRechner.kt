package de.auinger.einkst

/*
https://www.bmf-steuerrechner.de/ekst/eingabeformekst.xhtml

https://de.wikipedia.org/wiki/Kindergeld_(Deutschland)#Historische_Entwicklung

 */
class EinkommensteuerRechner {

    private val kindergeldRechner = KindergeldRechner()

    fun calculate(
        zvE: Int,
        splitting: Boolean,
        anzahlKinder: Int,
        params: BerechnungsParameter
    ): EinkommensteuerResult {
        val kindergeld = kindergeldRechner.berechneKindergeldProJahr(anzahlKinder = anzahlKinder, parameter = params)

        val einkommensteuerOhneKinderFreibetrag =
            if (splitting) {
                calculateEinkommensteuer(zvE / 2, params) * 2
            } else {
                calculateEinkommensteuer(zvE, params)
            }

        val zvE_reduziert = zvE - params.kinderfreibetrag * anzahlKinder
        val einkommensteuerMitKinderFreibetrag =
            if (splitting) {
                calculateEinkommensteuer(zvE_reduziert / 2, params) * 2
            } else {
                calculateEinkommensteuer(zvE_reduziert, params)
            }

        val ersparnisDurchKinderfreibetrag = einkommensteuerOhneKinderFreibetrag - einkommensteuerMitKinderFreibetrag
        return if (ersparnisDurchKinderfreibetrag > kindergeld) {
            val einkommensteuer = einkommensteuerMitKinderFreibetrag + kindergeld
            EinkommensteuerResult(
                einkommensteuer = einkommensteuer,
                soli = berechneSoli(einkommensteuer, splitting, params),
                kirchensteuer = 0
            )
        } else {
            EinkommensteuerResult(
                einkommensteuer = einkommensteuerOhneKinderFreibetrag,
                soli = berechneSoli(einkommensteuerMitKinderFreibetrag, splitting, params),
                kirchensteuer = 0
            )
        }
    }


    fun calculateEinkommensteuer(zvE: Int, params: BerechnungsParameter): Int {
        return when {
            zvE <= params.zveLimit1 -> 0
            zvE <= params.zveLimit2 -> {
                val y = (zvE - params.zveLimit1) / 10000.0
                ((980.14 * y + 1400) * y).toInt()
            }

            zvE <= params.zveLimit3 -> {
                val z = (zvE - 14254) / 10000.0
                ((216.16 * z + 2397) * z + 965.58).toInt()
            }

            zvE <= params.zveLimit4 -> {
                (0.42 * zvE - 8780.9).toInt()
            }

            else -> {
                (0.45 * zvE - 16740.68).toInt()
            }
        }
    }


    fun berechneSoli(einkommensteuerMitKinderFreibetrag: Int, splitting: Boolean, params: BerechnungsParameter): Int {
        val freibetrag = if (splitting) params.freibetragSoli * 2 else params.freibetragSoli
        return if (einkommensteuerMitKinderFreibetrag <= freibetrag) {
            0
        } else {
            (einkommensteuerMitKinderFreibetrag * 0.055).toInt()
        }
    }

}