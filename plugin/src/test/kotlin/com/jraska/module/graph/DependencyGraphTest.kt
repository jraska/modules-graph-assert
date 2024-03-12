package com.jraska.module.graph

import org.junit.Test

class DependencyGraphTest {
  @Test
  fun correctHeightIsMaintained() {
    val dependencyTree =
      DependencyGraph.create(
        "app" to "feature",
        "app" to "lib",
        "feature" to "lib",
        "lib" to "core",
      )

    assert(dependencyTree.heightOf("app") == 3)
  }

  @Test
  fun findsProperLongestPath() {
    val dependencyTree =
      DependencyGraph.create(
        "app" to "feature",
        "app" to "lib",
        "app" to "core",
        "feature" to "lib",
        "lib" to "core",
      )

    assert(dependencyTree.longestPath("app").nodeNames == listOf("app", "feature", "lib", "core"))
  }

  @Test
  fun findsProperRoot() {
    val dependencyTree =
      DependencyGraph.create(
        "feature" to "lib",
        "lib" to "core",
        "app" to "feature",
        "app" to "lib",
        "app" to "core",
      )

    assert(dependencyTree.findRoot().key == "app")
  }

  @Test
  fun createsSubtreeProperly() {
    val dependencyTree =
      DependencyGraph.create(
        "feature" to "lib",
        "lib" to "core",
        "app" to "feature",
        "feature" to "core",
        "app" to "core",
      )

    val subTree = dependencyTree.subTree("feature")

    assert(subTree.findRoot().key == "feature")
    assert(subTree.heightOf("feature") == 2)
    assert(subTree.longestPath("feature").nodeNames == listOf("feature", "lib", "core"))
  }

  @Test
  fun subtreeOfLeafModuleIsNotEmpty() {
    val dependencyTree =
      DependencyGraph.create(
        "feature" to "lib",
        "lib" to "core",
        "app" to "feature",
        "feature" to "core",
        "app" to "core",
      )

    assert(dependencyTree.subTree("core").findRoot().key == "core")
  }

  @Test
  fun singleModuleTreeWorks() {
    val dependencyGraph = DependencyGraph.createSingular(":app")

    assert(dependencyGraph.dependencyPairs().isEmpty())
    assert(dependencyGraph.height() == 0)
    assert(dependencyGraph.statistics().modulesCount == 1)
    assert(dependencyGraph.statistics().edgesCount == 0)
    assert(dependencyGraph.statistics().height == 0)
    assert(dependencyGraph.statistics().height == 0)
    assert(dependencyGraph.findRoot().key == ":app")
    assert(dependencyGraph.heightOf(":app") == 0)
    assert(dependencyGraph.longestPath().nodeNames == listOf(":app"))
    assert(dependencyGraph.longestPath().nodeNames == dependencyGraph.statistics().longestPath.nodeNames)
  }

  @Test(expected = IllegalArgumentException::class)
  fun cannotCreateEmptyGraph() {
    DependencyGraph.create()
  }

  @Test
  fun countsStatisticsWell() {
    val dependencyTree =
      DependencyGraph.create(
        ":app" to ":core",
        ":app" to ":core-android",
        ":app" to ":navigation",
        ":app" to ":lib:navigation-deeplink",
        ":app" to ":lib:identity",
        ":app" to ":lib:dynamic-features",
        ":app" to ":lib:network-status",
        ":app" to ":feature:push",
        ":app" to ":feature:users",
        ":app" to ":feature:settings_entrance",
        ":app" to ":feature:about_entrance",
        ":app" to ":feature:shortcuts",
        ":core-android" to ":core",
        ":lib:navigation-deeplink" to ":navigation",
        ":lib:navigation-deeplink" to ":core",
        ":lib:identity" to ":core",
        ":lib:dynamic-features" to ":core",
        ":lib:dynamic-features" to ":core-android",
        ":lib:network-status" to ":core",
        ":lib:network-status" to ":core-android",
        ":feature:push" to ":core",
        ":feature:push" to ":core-android",
        ":feature:push" to ":lib:identity",
        ":feature:users" to ":core",
        ":feature:users" to ":core-android",
        ":feature:users" to ":navigation",
        ":feature:settings_entrance" to ":core",
        ":feature:settings_entrance" to ":core-android",
        ":feature:settings_entrance" to ":lib:dynamic-features",
        ":feature:about_entrance" to ":core",
        ":feature:about_entrance" to ":core-android",
        ":feature:about_entrance" to ":lib:dynamic-features",
        ":feature:shortcuts" to ":core",
        ":feature:shortcuts" to ":core-android",
        ":core-testing" to ":core",
        ":feature:about" to ":app",
        ":feature:about" to ":core",
        ":feature:about" to ":core-android",
        ":feature:about" to ":navigation",
        ":feature:about" to ":lib:identity",
        ":feature:about" to ":lib:dynamic-features",
        ":feature:settings" to ":core",
        ":feature:settings" to ":core-android",
        ":feature:settings" to ":lib:dynamic-features",
        ":feature:settings" to ":app",
      )

    val statistics = dependencyTree.statistics()

    assert(statistics.height == 5)
    assert(statistics.modulesCount == 16)
    assert(statistics.edgesCount == 45)
    assert(
      statistics.longestPath.nodeNames ==
        listOf(
          ":feature:settings",
          ":app",
          ":feature:settings_entrance",
          ":lib:dynamic-features",
          ":core-android",
          ":core",
        ),
    )
  }
}
