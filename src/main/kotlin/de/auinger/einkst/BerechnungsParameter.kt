package de.auinger.einkst

data class BerechnungsParameter(
        val zveLimit1: Int,
        val zveLimit2: Int,
        val zveLimit3: Int,
        val zveLimit4: Int,
        val kinderfreibetrag: Int,
        val kindergeldKind1: Int,
        val kindergeldKind2: Int,
        val kindergeldKind3: Int,
        val kindergeldKindWeiteres: Int,
        val freibetragSoli: Int,
        val minBeitragbemessungsgrenzeKrankenkasse: Int,
        val maxBeitragbemessungsgrenzeKrankenkasse: Int
) {
    companion object {
        val year2019 = BerechnungsParameter(
                zveLimit1 = 9_168,
                zveLimit2 = 14_254,
                zveLimit3 = 55_960,
                zveLimit4 = 265_326,
                kinderfreibetrag = 7_620,
                kindergeldKind1 = 199,
                kindergeldKind2 = 199,
                kindergeldKind3 = 205,
                kindergeldKindWeiteres = 230,
                freibetragSoli = 972,
                minBeitragbemessungsgrenzeKrankenkasse = 12_740,
                maxBeitragbemessungsgrenzeKrankenkasse = 54_450
        )
    }
}
