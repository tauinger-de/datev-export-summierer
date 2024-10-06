package de.auinger.datev.summierer

class ParseException(line: String, cause: Throwable) : RuntimeException("Error parsing line '$line': ", cause)
