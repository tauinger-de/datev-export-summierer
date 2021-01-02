package de.auinger.einkst

data class Ergebnis(
        var einkommensteuer:Int = 0,
        var soli:Int = 0,
        var kirchensteuer:Int = 0,
        var krankenkassenbeitrag: Int = 0,
        var pflegeversicherungsbeitrag: Int = 0
) {

}
