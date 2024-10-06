package de.auinger.einkst

data class Eingaben(
    val ueberschuss: Int,
    val zusaetzlichesZvE: Int,
    val krankenkassenGesamtBeitragssatz: Double,
    val anzahlKinder: Int
)
