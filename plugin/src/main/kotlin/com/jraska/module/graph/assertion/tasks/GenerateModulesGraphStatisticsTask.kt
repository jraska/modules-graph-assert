package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.DependencyGraph
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

open class GenerateModulesGraphStatisticsTask : DefaultTask() {
  @Optional
  @Input
  var onlyModuleStatistics: String? = null

  @Input
  lateinit var dependencyGraph: DependencyGraph.SerializableGraph

  @TaskAction
  fun run() {
    val dependencyGraph = DependencyGraph.create(dependencyGraph)

    if (onlyModuleStatistics == null) {
      println(dependencyGraph.statistics())
    } else {
      println(dependencyGraph.subTree(onlyModuleStatistics!!).statistics())
    }
  }
}
