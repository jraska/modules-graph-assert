package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.GraphvizWriter
import com.jraska.module.graph.assertion.Api
import com.jraska.module.graph.assertion.GradleDependencyGraphFactory
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateModulesGraphTask : DefaultTask() {
  @Input
  var layers: Array<String> = emptyArray()

  @TaskAction
  fun run() {
    val dependencyGraph = createDependencyGraph()

    if (shouldPrintStatistics()) {
      println(dependencyGraph.statistics())
    }
    val graphviz = GraphvizWriter.toGraphviz(dependencyGraph, layers.toSet())

    if (shouldOutputFile()) {
      getOutputFile().apply {
          println("GraphViz saved to $path")
          writeText(graphviz)
        }
    } else {
      println(graphviz)
    }
  }

  private fun createDependencyGraph(): DependencyGraph {
    val dependencyGraph = GradleDependencyGraphFactory.create(project)

    if (project.hasProperty(Api.Parameters.PRINT_ONLY_MODULE)) {
      val moduleName = project.property(Api.Parameters.PRINT_ONLY_MODULE) as String?
      if (moduleName != null) {
        return dependencyGraph.subTree(moduleName)
      }
    }

    return dependencyGraph
  }

  private fun shouldPrintStatistics(): Boolean {
    return project.hasProperty(Api.Parameters.PRINT_STATISTICS) && project.property(Api.Parameters.PRINT_STATISTICS) == "true"
  }
  private fun shouldOutputFile(): Boolean {
    return project.hasProperty(Api.Parameters.OUTPUT_PATH)
  }

  private fun getOutputFile(): File {
    return File(project.property(Api.Parameters.OUTPUT_PATH).toString())
  }
}
