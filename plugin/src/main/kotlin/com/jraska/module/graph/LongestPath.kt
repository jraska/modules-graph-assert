package com.jraska.module.graph

class LongestPath(
  val nodeNames: List<String>
) {
  fun pathString(): String {
    return nodeNames.joinToString(" -> ")
  }

  override fun toString() = "'${pathString()}'"
}
