package de.auinger.einkst

class KindergeldRechner {

    fun berechneKindergeldProJahr(anzahlKinder: Int, parameter: BerechnungsParameter): Int {
        return when {
            anzahlKinder <= 0 -> 0
            anzahlKinder == 1 -> parameter.kindergeldKind1 * 12
            anzahlKinder == 2 -> parameter.kindergeldKind2 * 12 + berechneKindergeldProJahr(anzahlKinder - 1, parameter)
            anzahlKinder == 3 -> parameter.kindergeldKind3 * 12 + berechneKindergeldProJahr(anzahlKinder - 1, parameter)
            anzahlKinder > 3 -> parameter.kindergeldKindWeiteres * 12 + berechneKindergeldProJahr(
                anzahlKinder - 1,
                parameter
            )

            else -> 0
        }
    }
}