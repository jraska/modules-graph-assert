package com.jraska.module.graph.writer

import com.jraska.module.graph.DependencyGraph

interface GraphWriter {
  fun toGraph(
    dependencyGraph: DependencyGraph,
    aliases: Map<String, String> = emptyMap(),
  ): String

  val graphDirection
    get() = Direction.TD

  enum class Direction {
    LR,
    TD,
  }
}
