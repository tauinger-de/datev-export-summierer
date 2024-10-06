package de.auinger.datev.summierer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

internal class SummaryItemTest {

    @Test
    fun getOverallAmount() {
        val summaryItem = SummaryItem(
            datum = LocalDate.now(),
            belegNr = "1",
            buchungsDetail = "Blah"
        )
        summaryItem.add(BigDecimal.ONE, Type.ERLOES_NETTO)
        summaryItem.add(BigDecimal.TEN, Type.ERLOES_UMST)
        assertEquals(BigDecimal("11"), summaryItem.overallAmount)
    }

}