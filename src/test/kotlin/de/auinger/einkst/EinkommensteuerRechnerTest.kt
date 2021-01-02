package de.auinger.einkst

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class EinkommensteuerRechnerTest {

    @Test
    fun calculate_single() {
        val result1 = EinkommensteuerRechner().calculate(5555, false, 2, BerechnungsParameter.year2019)
        Assertions.assertEquals(result1.einkommensteuer, 0)

        val result2 = EinkommensteuerRechner().calculate(11111, false, 2, BerechnungsParameter.year2019)
        Assertions.assertEquals(result2.einkommensteuer, 309)

        val result3 = EinkommensteuerRechner().calculate(22222, false, 2, BerechnungsParameter.year2019)
        Assertions.assertEquals(result3.einkommensteuer, 3012)

        val result4 = EinkommensteuerRechner().calculate(42961, false, 2, BerechnungsParameter.year2019)
        Assertions.assertEquals(result4.einkommensteuer, 9628)

        val result5 = EinkommensteuerRechner().calculate(88888, false, 2, BerechnungsParameter.year2019)
        Assertions.assertEquals(result5.einkommensteuer, 28552)

        val result6 = EinkommensteuerRechner().calculate(333_333, false, 2, BerechnungsParameter.year2019)
        Assertions.assertEquals(result6.einkommensteuer, 133259)
    }


    @Test
    fun calculate_splitting() {
        val result1 = EinkommensteuerRechner().calculate(5555, true, 2, BerechnungsParameter.year2019)
        assertThat(result1.einkommensteuer).isEqualTo(0)

        val result2 = EinkommensteuerRechner().calculate(11111, true, 2, BerechnungsParameter.year2019)
        assertThat(result2.einkommensteuer).isEqualTo(0)

        val result3 = EinkommensteuerRechner().calculate(22222, true, 2, BerechnungsParameter.year2019)
        assertThat(result3.einkommensteuer).isEqualTo(618)

        val result4 = EinkommensteuerRechner().calculate(42961, true, 2, BerechnungsParameter.year2019)
        assertThat(result4.einkommensteuer).isEqualTo(5620)
        assertThat(result4.soli).isEqualTo(0)

        val result5 = EinkommensteuerRechner().calculate(88888, true, 2, BerechnungsParameter.year2019)
        assertThat(result5.einkommensteuer).isEqualTo(20344)
        assertThat(result5.soli).isEqualTo(1118)

        val result6 = EinkommensteuerRechner().calculate(333_333, true, 2, BerechnungsParameter.year2019)
        Assertions.assertEquals(result6.einkommensteuer, 122436)
    }

}