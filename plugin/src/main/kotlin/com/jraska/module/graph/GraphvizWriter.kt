package com.jraska.module.graph

object GraphvizWriter {
  fun toGraphviz(dependencyGraph: DependencyGraph): String {

    val longestPathConnections = dependencyGraph.longestPath()
      .nodeNames.zipWithNext()
      .toSet()

    val stringBuilder = StringBuilder()

    stringBuilder.append("digraph G {\n")

    dependencyGraph.dependencyPairs()
      .forEach { connection ->
        stringBuilder.append("\"${connection.first}\"")
          .append(" -> ")
          .append("\"${connection.second}\"")

        if (longestPathConnections.contains(connection)) {
          stringBuilder.append(" [color=red style=bold]")
        }

        stringBuilder.append("\n")
      }

    stringBuilder.append("}")

    return stringBuilder.toString()
  }
}
