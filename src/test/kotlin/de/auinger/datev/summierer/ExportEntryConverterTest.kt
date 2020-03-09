package de.auinger.datev.summierer

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class ExportEntryConverterTest {

    @Test
    fun convert() {
        val exportEntry = ExportEntryConverter().convert("123,45;S;2;3;4;5;6;8400;0303")
        Assertions.assertEquals(BigDecimal("123.45"), exportEntry.umsatz)
        Assertions.assertEquals(SollHaben.S, exportEntry.sollHaben)
        Assertions.assertEquals(8400, exportEntry.gegenkonto)
        Assertions.assertEquals(3, exportEntry.monat)
    }
}
