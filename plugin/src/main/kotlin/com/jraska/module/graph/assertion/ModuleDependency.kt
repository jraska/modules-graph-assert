package com.jraska.module.graph.assertion

class ModuleDependency(
  val dependencyPair: Pair<String, String>,
  private val fromAlias: String?,
  private val toAlias: String?,
) {
  private val from get() = dependencyPair.first
  private val to get() = dependencyPair.second

  fun pairToAssert(): Pair<String, String> {
    if (fromAlias != null || toAlias != null) {
      return (fromAlias ?: from) to (toAlias ?: to)
    } else {
      return dependencyPair
    }
  }

  fun assertDisplayText(): String {
    val stringBuilder = StringBuilder()

    if (fromAlias != null) {
      stringBuilder.append("\"$fromAlias\"")
      stringBuilder.append("('$from')")
    } else {
      stringBuilder.append("'$from'")
    }

    stringBuilder.append(" -> ")

    if (toAlias != null) {
      stringBuilder.append("\"$toAlias\"")
      stringBuilder.append("('$to')")
    } else {
      stringBuilder.append("'$to'")
    }

    return stringBuilder.toString()
  }

  fun fromDocText(): String {
    return if (fromAlias != null) {
      "$from('$fromAlias')"
    } else {
      from
    }
  }

  fun toDocText(): String {
    return if (toAlias != null) {
      "$to('$toAlias')"
    } else {
      to
    }
  }

  fun fromDocTextMermaid(): String {
    return if (fromAlias != null) {
      "$from($fromAlias)"
    } else {
      "$from($from)"
    }
  }

  fun toDocTextMermaid(): String {
    return if (toAlias != null) {
      "$to($toAlias)"
    } else {
      "$to($to)"
    }
  }
}
