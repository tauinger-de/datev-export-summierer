package de.auinger.datev.summierer

import java.lang.RuntimeException

class ParseException(line:String, cause: Throwable) : RuntimeException("Error parsing line '$line': ", cause)
