package com.jraska.module.graph

object GraphvizWriter {
  fun toGraphviz(dependencyGraph: DependencyGraph, groups: Set<String> = emptySet()): String {

    val longestPathConnections = dependencyGraph.longestPath()
      .nodeNames.zipWithNext()
      .toSet()

    val stringBuilder = StringBuilder()

    stringBuilder.append("digraph G {\n")

    if(groups.isNotEmpty()) {
      stringBuilder.append("ranksep = 1.5\n")
    }
    groups.forEach {
      stringBuilder.append(generateGroup(dependencyGraph, it))
    }

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

  private fun generateGroup(dependencyGraph: DependencyGraph, groupName: String): String {
    val builder = StringBuilder()
      .append("subgraph cluster_").append(groupName.replace(":", "")).appendln("{")
      .appendln("style = filled;")
      .appendln("color = lightgrey;")
      .appendln("node[style = filled, color = white];")
      .append("label = \"").append(groupName).appendln("\"")

    dependencyGraph.nodes().filter { it.key.startsWith(groupName) }.forEach {
      builder.append("\"").append(it.key).appendln("\"")
    }

    return builder.appendln("}")
      .appendln()
      .toString()
  }
}
