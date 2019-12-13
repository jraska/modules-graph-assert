package com.jraska.module.graph

object KotlinCodeWriter {
  const val SPACING = "  "

  fun toKotlinCode(graph: DependencyGraph): String {
    val pairs = graph.nodes().flatMap { node ->
      node.dependsOn.map { node.key to it.key }
    }.joinToString(
      separator = ",\n",
      prefix = "listOf(\n",
      postfix = "\n)",
      transform = { "  \"${it.first}\" to \"${it.second}\"" })

    return pairs
  }
}
