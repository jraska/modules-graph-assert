package com.jraska.module.graph.writer

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.assertion.mapAlias

object MermaidWriter : GraphWriter {
  override fun toGraph(
    dependencyGraph: DependencyGraph,
    aliases: Map<String, String>,
  ): String {
    val longestPathConnections =
      dependencyGraph.longestPath()
        .nodeNames.zipWithNext()
        .toSet()

    val stringBuilder = StringBuilder()
    val styles = StringBuilder()

    /**
     * LR: left to right
     * TD: top down
     */
    stringBuilder
      .append("```mermaid\n")
      .append("graph $graphDirection;\n")

    val dependencyPairs = dependencyGraph.dependencyPairs()
    if (dependencyPairs.isEmpty()) {
      stringBuilder
        .append("\"${dependencyGraph.findRoot().key}\"")
        .append("\n")
    }

    dependencyPairs
      .map { aliases.mapAlias(it) }
      .forEachIndexed { index, connection ->
        stringBuilder.append(connection.fromDocTextMermaid())
          .append(" --> ")
          .append("${connection.toDocTextMermaid()};")
          .append("\n")

        if (longestPathConnections.contains(connection.dependencyPair)) {
          styles.append("linkStyle $index stroke-width:2px,stroke:red;\n")
        }
      }

    styles
      .append("linkStyle default stroke-width:1px;\n")
      .append("```\n")

    stringBuilder.append("\n").append(styles)

    return stringBuilder.toString()
  }

  override val graphDirection = GraphWriter.Direction.LR
}
