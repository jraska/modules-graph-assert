package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.GraphvizWriter
import com.jraska.module.graph.assertion.Api
import com.jraska.module.graph.assertion.GradleDependencyGraphFactory
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class GenerateModulesGraphTask : DefaultTask() {
  @Input
  var layers: Array<String> = emptyArray()

  @TaskAction
  fun run() {
    val dependencyGraph = createDependencyGraph()

    if (shouldPrintStatistics()) {
      println(dependencyGraph.statistics())
    }

    println(GraphvizWriter.toGraphviz(dependencyGraph, layers.toSet()))
  }

  private fun createDependencyGraph(): DependencyGraph {
    val dependencyGraph = GradleDependencyGraphFactory.create(project)

    val moduleName = project.property(Api.Parameters.PRINT_ONLY_MODULE) as String?
    if (moduleName != null) {
      return dependencyGraph.subTree(moduleName)
    }

    return dependencyGraph
  }

  private fun shouldPrintStatistics(): Boolean {
    return project.property(Api.Parameters.PRINT_STATISTICS) == true
  }
}
