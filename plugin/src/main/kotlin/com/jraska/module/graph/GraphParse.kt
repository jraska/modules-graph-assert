package com.jraska.module.graph

import java.util.regex.Pattern

object GraphParse {
  private val DEPENDENCY_FORMAT = Pattern.compile("([^\\s]+) -> ([^\\s]+)") // :feature -> :other-feature

  fun parse(dependencyText: String): Pair<String, String> {
    val matcher = DEPENDENCY_FORMAT.matcher(dependencyText)

    if (!matcher.find()) {
      throw IllegalArgumentException("$dependencyText is not in \"${DEPENDENCY_FORMAT.pattern()}\" format.")
    }

    return matcher.group(1) to matcher.group(2)
  }
}
