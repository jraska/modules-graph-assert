package com.jraska.module.graph.assertion

fun String.extractRelations() =
  split("\n")
    .drop(1)
    .dropLast(1)

fun String.assertRelations(expected: List<String>) {
  val relations = extractRelations()
  assert(relations.size == expected.size)
  assert(relations.containsAll(expected))
}
