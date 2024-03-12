package com.jraska.module.graph

data class GraphStatistics(
  val modulesCount: Int,
  val edgesCount: Int,
  val height: Int,
  val longestPath: LongestPath,
)
