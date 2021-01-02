package de.auinger.datev.summierer

import de.auinger.einkst.BerechnungsParameter
import de.auinger.einkst.Eingaben
import de.auinger.einkst.Rechner

fun main(args:Array<String>) {
    val rechner = Rechner(
            eingaben = Eingaben(
                    ueberschuss = 67_338,
                    anzahlKinder = 2,
                    krankenkassenGesamtBeitragssatz = 15.9,
                    sonstigeSteuerminderndeBetraege = 0
            ),
            parameter = BerechnungsParameter.year2019
    )
    println(rechner.calc())
}
