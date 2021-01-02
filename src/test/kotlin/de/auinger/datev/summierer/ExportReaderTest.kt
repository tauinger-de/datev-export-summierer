package de.auinger.datev.summierer

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

internal class ExportReaderTest {

    private val exportData = """55,33;"H";"";;;"";1890;4670;"";0301;"B20-223";"";;"Reisekosten Unternehmer";;"";;;;"";"Belegnummer";"B20-223";"Name";"LASTSCHRIFT, EREF+0001356097VVM20200145756 MREF+VVM12946 CRED+DE39DBV00000002177 ABWA+DB Vertrieb GmbH SVWZ+Abo17308 Kd12946 EREF: 0001356097VVM20200145756 MREF: VVM12946 CRED: DE39DBV00000002177 IBAN: DE25";"Beschreibung";"";"Anlass";"";"Teilnehmer";"";"Steuersatz";"";"Abschreibungsinfo";"";"";"";"";"";;"";;"";;;;;;"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";"";;;;"";;;;"";"";;"";;;;"";"";;"";;"";;"";"";;"";;;;"""

    @Test
    fun readEntries() {
        val entries = ExportReader().readEntries(contentString = exportData, jahr = 2020)
        assertEquals(1, entries.size)

        val exportEntry = entries[0]
        assertEquals(BigDecimal("55.33"), exportEntry.umsatz)
        assertEquals(SollHaben.H, exportEntry.sollHaben)
        assertEquals(4670, exportEntry.gegenkonto)
        assertEquals(3, exportEntry.tag)
        assertEquals(1, exportEntry.monat)
        assertEquals("B20-223", exportEntry.belegfeld1)
        assertEquals("Reisekosten Unternehmer", exportEntry.buchungsText)
        assertEquals("LASTSCHRIFT, EREF+0001356097VVM20200145756 MREF+VVM12946 CRED+DE39DBV00000002177 ABWA+DB Vertrieb GmbH SVWZ+Abo17308 Kd12946 EREF: 0001356097VVM20200145756 MREF: VVM12946 CRED: DE39DBV00000002177 IBAN: DE25", exportEntry.buchungsDetail)
    }
}