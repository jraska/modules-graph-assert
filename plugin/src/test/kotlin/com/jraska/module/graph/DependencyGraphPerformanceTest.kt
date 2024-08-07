package com.jraska.module.graph

import org.junit.Before
import org.junit.Test
import java.io.File

class DependencyGraphPerformanceTest {
  lateinit var dependencyGraph: DependencyGraph

  @Before
  fun setUp() {
    val uri = javaClass.classLoader.getResource("graph/large-graph.txt")
    val file = File(uri?.path!!)
    val dependencyPairs = file.readLines().map {
      val parts = it.split(" -> ")
      parts[0] to parts[1]
    }

    dependencyGraph = DependencyGraph.create(dependencyPairs)
  }

  @Test(timeout = 1000) // 1000 ms is more than enough - it was taking hours before optimisations
  fun whenTheGraphIsLarge_statisticsCalculatedFast() {
    val statistics = dependencyGraph.statistics()

    assert(statistics.height == 59)
    assert(statistics.modulesCount == 1000)
    assert(statistics.edgesCount == 15259)
    assert(statistics.longestPath.pathString().startsWith("23 -> 31 -> 36 -> 57 -> 61 -> 72 -> 74 -> 75"))
  }

  @Test(timeout = 1_000)
  fun whenTheGraphIsLarge_statisticsOfSubgraphMatchFast() {
    val subGraphStatistics = dependencyGraph.subTree("31").statistics()

    assert(subGraphStatistics.height == 58)
    assert(subGraphStatistics.longestPath.pathString().startsWith("31 -> 36 -> 57 -> 61 -> 72 -> 74 -> 75"))
  }

  @Test(timeout = 1_000)
  fun whenTheGraphIsLarge_statisticsCreatedFast() {
    val subGraphStatistics = dependencyGraph.subTree("500").statistics()
    assert(subGraphStatistics.modulesCount == 281)
  }

  @Test(timeout = 1_000) // was running out of heap before optimisation
  fun whenTheGraphIsLarge_statisticsLargeCreatedFast() {
    val subGraphStatistics = dependencyGraph.subTree("2").statistics()

    assert(subGraphStatistics.modulesCount == 870)
    assert(subGraphStatistics.edgesCount == 11650)
    assert(subGraphStatistics.height == 55)
    assert(subGraphStatistics.longestPath.pathString().startsWith("2 -> 30 -> 76 -> 105 -> 119 -> "))
  }
}
