package com.jraska.module.graph

import kotlinx.serialization.Serializable

@Serializable
data class LongestPath(
  val nodeNames: List<String>
) {
  fun pathString(): String {
    return nodeNames.joinToString(" -> ")
  }

  override fun toString() = "'${pathString()}'"
}
