package com.jraska.module.graph

import kotlinx.serialization.Serializable

@Serializable
data class GraphStatistics(
  val module: String,
  val modulesCount: Int,
  val edgesCount: Int,
  val height: Int,
  val longestPath: LongestPath,
)
