package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.GraphvizWriter
import com.jraska.module.graph.assertion.Api
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateModulesGraphTask : DefaultTask() {

  @Input
  lateinit var aliases: Map<String, String>

  @Optional
  @Input
  var onlyModuleToPrint: String? = null

  @Optional
  @Input
  var onlyPrintDirectDependencies: Boolean? = false

  @Input
  lateinit var dependencyGraph: DependencyGraph.SerializableGraph

  @Optional
  @Input
  var outputFilePath: String? = null

  @Optional
  @OutputFile
  var outputFile: File? = null

  @TaskAction
  fun run() {
    val dependencyGraph: DependencyGraph = DependencyGraph.create(dependencyGraph).let {
      val moduleToPrint = onlyModuleToPrint
      if (moduleToPrint == null) {
        it
      } else {
        val subTree = it.subTree(moduleToPrint)

        // Check if we should only print direct dependencies of the root of the given subtree.
        if (this.onlyPrintDirectDependencies == true) {
          subTree.keepDirectDependencies(moduleToPrint)
        } else {
          subTree
        }
      }
    }

    val graphviz = GraphvizWriter.toGraphviz(dependencyGraph, aliases)

    if (outputFilePath != null) {
      val file = File(outputFilePath!!)
      file.writeText(graphviz)
      outputFile = file
      println("GraphViz saved to $path")
    } else {
      println(graphviz)
    }
  }

  companion object {
    internal fun outputFilePath(project: Project): String? {
      if (project.hasProperty(Api.Parameters.OUTPUT_PATH)) {
        return project.property(Api.Parameters.OUTPUT_PATH).toString()
      } else {
        return null
      }
    }

    internal fun onlyModule(project: Project): String? {
      if (project.hasProperty(Api.Parameters.PRINT_ONLY_MODULE)) {
        return project.property(Api.Parameters.PRINT_ONLY_MODULE) as String?
      } else {
        return null
      }
    }

    internal fun onlyDirectDependencies(project: Project): Boolean {
      if (project.hasProperty(Api.Parameters.PRINT_ONLY_DIRECT_DEPENDENCIES)) {
        val propValue = project.property(Api.Parameters.PRINT_ONLY_DIRECT_DEPENDENCIES).toString()
        return propValue == "true"
      } else {
        return false
      }
    }
  }
}
