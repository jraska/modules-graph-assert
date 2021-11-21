package com.jraska.module.graph.assertion

class DependencyToAssert(
  private val dependencyPair: Pair<String, String>,
  private val fromAlias: String?,
  private val toAlias: String?
) {
  fun pairToAssert(): Pair<String, String> {
    if (fromAlias != null || toAlias != null) {
      return (fromAlias ?: dependencyPair.first) to (toAlias ?: dependencyPair.second)
    } else {
      return dependencyPair
    }
  }

  fun displayText(): String {
    val stringBuilder = StringBuilder()

    if (fromAlias != null) {
      stringBuilder.append("\"$fromAlias\"")
      stringBuilder.append("('${dependencyPair.first}')")
    } else {
      stringBuilder.append("'${dependencyPair.first}'")
    }

    stringBuilder.append(" -> ")

    if (toAlias != null) {
      stringBuilder.append("\"$toAlias\"")
      stringBuilder.append("('${dependencyPair.second}')")
    } else {
      stringBuilder.append("'${dependencyPair.second}'")
    }

    return stringBuilder.toString()
  }
}
