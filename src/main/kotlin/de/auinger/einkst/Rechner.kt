package de.auinger.einkst

/*
https://www.krankenkassen.de/gesetzliche-krankenkassen/krankenkasse-beitrag/selbststaendige/
https://de.wikipedia.org/wiki/Beitragsbemessungsgrenze

 */
class Rechner(
    private val eingaben: Eingaben,
    private val parameter: BerechnungsParameter
) {

    private val ergebnis = Ergebnis()


    fun calc(): Ergebnis {
        calcKrankenkassenbeitrag()
        calcPflegeversicherungsbeitrag()
        calcEinkommensteuer()
        return this.ergebnis
    }


    private fun calcKrankenkassenbeitrag() {
        val basis = Math.max(
            parameter.minBeitragbemessungsgrenzeKrankenkasse,
            Math.min(
                eingaben.ueberschuss,
                parameter.maxBeitragbemessungsgrenzeKrankenkasse
            )
        )
        ergebnis.krankenkassenbeitrag = (basis * eingaben.krankenkassenGesamtBeitragssatz / 100.0).toInt()
    }


    private fun calcPflegeversicherungsbeitrag() {
        val basis = Math.max(
            parameter.minBeitragbemessungsgrenzeKrankenkasse,
            Math.min(
                eingaben.ueberschuss,
                parameter.maxBeitragbemessungsgrenzeKrankenkasse
            )
        )
        val satz = if (eingaben.anzahlKinder == 0) 3.30 else 3.05
        ergebnis.pflegeversicherungsbeitrag = (basis * satz / 100.0).toInt()
    }


    private fun calcEinkommensteuer() {
        // prep
        val kindergeld = kindergeld(anzahlKinder = eingaben.anzahlKinder)
        val kinderfreibetrag = parameter.kinderfreibetrag * eingaben.anzahlKinder
        val zvE =
            eingaben.ueberschuss - ergebnis.krankenkassenbeitrag - ergebnis.pflegeversicherungsbeitrag + eingaben.zusaetzlichesZvE

        val einkommensteuerOhneKinderFreibetrag =
            if (eingaben.anzahlKinder > 0) {
                einkommensteuerVon(zvE / 2) * 2
            } else {
                einkommensteuerVon(zvE)
            }
        val einkommensteuerMitKinderFreibetrag =
            if (eingaben.anzahlKinder > 0) {
                einkommensteuerVon((zvE - kinderfreibetrag) / 2) * 2
            } else {
                einkommensteuerVon(zvE - kinderfreibetrag)
            }
        val ersparnisDurchKinderfreibetrag = einkommensteuerOhneKinderFreibetrag - einkommensteuerMitKinderFreibetrag

        ergebnis.einkommensteuer = if (ersparnisDurchKinderfreibetrag > kindergeld) {
            einkommensteuerMitKinderFreibetrag + kindergeld
        } else {
            einkommensteuerOhneKinderFreibetrag
        }
        ergebnis.soli = soliVon(einkommensteuerMitKinderFreibetrag)
        ergebnis.kirchensteuer = kirchensteuerVon(einkommensteuerMitKinderFreibetrag)
    }


    private fun einkommensteuerVon(zvE: Int): Int {
        return when {
            zvE <= parameter.zveLimit1 -> 0
            zvE <= parameter.zveLimit2 -> {
                val y = (zvE - parameter.zveLimit1) / 10000.0
                ((980.14 * y + 1400) * y).toInt()
            }

            zvE <= parameter.zveLimit3 -> {
                val z = (zvE - 14254) / 10000.0
                ((216.16 * z + 2397) * z + 965.58).toInt()
            }

            zvE <= parameter.zveLimit4 -> {
                (0.42 * zvE - 8780.9).toInt()
            }

            else -> {
                (0.45 * zvE - 16740.68).toInt()
            }
        }
    }


    private fun soliVon(einkommensteuer: Int): Int {
        val freibetrag = if (eingaben.anzahlKinder > 0) parameter.freibetragSoli * 2 else parameter.freibetragSoli
        return if (einkommensteuer <= freibetrag) {
            0
        } else {
            (einkommensteuer * 0.055).toInt()
        }
    }


    private fun kirchensteuerVon(einkommensteuer: Int): Int {
        return (einkommensteuer * 0.08).toInt()
    }


    fun kindergeld(anzahlKinder: Int): Int {
        return when {
            anzahlKinder <= 0 -> 0
            anzahlKinder == 1 -> parameter.kindergeldKind1 * 12
            anzahlKinder == 2 -> parameter.kindergeldKind2 * 12 + kindergeld(anzahlKinder - 1)
            anzahlKinder == 3 -> parameter.kindergeldKind3 * 12 + kindergeld(anzahlKinder - 1)
            anzahlKinder > 3 -> parameter.kindergeldKindWeiteres * 12 + kindergeld(anzahlKinder - 1)
            else -> 0
        }
    }

}
