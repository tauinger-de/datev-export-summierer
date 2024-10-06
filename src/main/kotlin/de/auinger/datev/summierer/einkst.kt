package de.auinger.datev.summierer

import de.auinger.einkst.BerechnungsParameter
import de.auinger.einkst.Eingaben
import de.auinger.einkst.Rechner

fun main() {
    val rechner = Rechner(
        eingaben = Eingaben(
            ueberschuss = 67_338,
            zusaetzlichesZvE = 10_000,
            anzahlKinder = 2,
            krankenkassenGesamtBeitragssatz = 15.9
        ),
        parameter = BerechnungsParameter.year2020
    )
    println(rechner.calc())
}
