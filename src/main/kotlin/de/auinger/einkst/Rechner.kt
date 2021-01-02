package de.auinger.einkst

/*
https://www.krankenkassen.de/gesetzliche-krankenkassen/krankenkasse-beitrag/selbststaendige/
https://de.wikipedia.org/wiki/Beitragsbemessungsgrenze

 */
class Rechner(
        private val eingaben:Eingaben,
        private val parameter:BerechnungsParameter
) {

    private val ergebnis = Ergebnis()


    fun calc(): Ergebnis {
        calcKrankenkassenbeitrag()
        calcPflegeversicherungsbeitrag()
        return this.ergebnis
    }


    private fun calcKrankenkassenbeitrag() {
        val basis = Math.max(
                parameter.minBeitragbemessungsgrenzeKrankenkasse,
                Math.min(
                        eingaben.ueberschuss,
                        parameter.maxBeitragbemessungsgrenzeKrankenkasse))
        ergebnis.krankenkassenbeitrag = (basis * eingaben.krankenkassenGesamtBeitragssatz / 100.0).toInt()
    }


    private fun calcPflegeversicherungsbeitrag() {
        val basis = Math.max(
                parameter.minBeitragbemessungsgrenzeKrankenkasse,
                Math.min(
                        eingaben.ueberschuss,
                        parameter.maxBeitragbemessungsgrenzeKrankenkasse))
        val satz = if (eingaben.anzahlKinder == 0) 3.30 else 3.05
        ergebnis.pflegeversicherungsbeitrag = (basis * satz / 100.0).toInt()
    }

}