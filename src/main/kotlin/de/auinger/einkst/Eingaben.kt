package de.auinger.einkst

data class Eingaben(
        val ueberschuss:Int,
        val krankenkassenGesamtBeitragssatz:Double,
        val sonstigeSteuerminderndeBetraege:Int,
        val anzahlKinder:Int
)
