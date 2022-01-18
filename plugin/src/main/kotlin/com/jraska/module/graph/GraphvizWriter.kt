package com.jraska.module.graph

import com.jraska.module.graph.assertion.mapAlias

object GraphvizWriter {
  fun toGraphviz(dependencyGraph: DependencyGraph, aliases: Map<String, String> = emptyMap()): String {

    val longestPathConnections = dependencyGraph.longestPath()
      .nodeNames.zipWithNext()
      .toSet()

    val stringBuilder = StringBuilder()

    stringBuilder.append("digraph G {\n")

    val dependencyPairs = dependencyGraph.dependencyPairs()
    if(dependencyPairs.isEmpty()) {
      stringBuilder.append("\"${dependencyGraph.findRoot().key}\"")
        .append("\n")
    }

    dependencyPairs
      .map { aliases.mapAlias(it) }
      .forEach { connection ->
        stringBuilder.append("\"${connection.fromDocText()}\"")
          .append(" -> ")
          .append("\"${connection.toDocText()}\"")

        if (longestPathConnections.contains(connection.dependencyPair)) {
          stringBuilder.append(" [color=red style=bold]")
        }

        stringBuilder.append("\n")
      }

    stringBuilder.append("}")

    return stringBuilder.toString()
  }
}
