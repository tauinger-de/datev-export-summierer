package de.auinger.datev.summierer

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(value = [
    "amount",
    "sollHaben"
])
data class ExportEntry(
        val amount: String,
        val sollHaben: String
) {

    constructor() :
            this("", "")

}
