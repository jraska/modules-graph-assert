package com.jraska.module.graph

object GraphvizWriter {
  fun toGraphviz(dependencyGraph: DependencyGraph): String {

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
