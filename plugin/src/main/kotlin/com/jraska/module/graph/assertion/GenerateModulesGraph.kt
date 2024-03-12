package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import org.gradle.api.Project
import java.io.File

class GenerateModulesGraph(
  var aliases: Map<String, String>,
  var onlyModuleToPrint: String? = null,
  var dependencyGraph: DependencyGraph.SerializableGraph,
  var outputFilePath: String? = null,
  var outputFile: File? = null,
  var outputFormat: OutputFormat = OutputFormat.GRAPHVIZ,
) {
  fun run(path: String) {
    val dependencyGraph =
      DependencyGraph.create(dependencyGraph).let {
        if (onlyModuleToPrint == null) {
          it
        } else {
          it.subTree(onlyModuleToPrint!!)
        }
      }

    val writer = outputFormat.writer.objectInstance
    if (writer == null) {
      print("No writer found for $outputFormat")
      return
    }

    val graph = writer.toGraph(dependencyGraph, aliases)

    if (outputFilePath != null) {
      val file = File(outputFilePath!!)
      file.writeText(graph)
      outputFile = file
      println("Graph saved to $path")
    } else {
      println(graph)
    }
  }

  companion object {
    internal fun outputFilePath(
      project: Project,
      outputFormat: OutputFormat,
    ): String? {
      return when {
        project.hasProperty(Api.Parameters.OUTPUT_PATH) && outputFormat.isGraphviz -> {
          project.property(Api.Parameters.OUTPUT_PATH).toString()
        }

        project.hasProperty(Api.Parameters.OUTPUT_PATH_MERMAID) && outputFormat.isMermaid -> {
          project.property(Api.Parameters.OUTPUT_PATH_MERMAID).toString()
        }

        else -> null
      }
    }

    internal fun onlyModule(project: Project): String? {
      if (project.hasProperty(Api.Parameters.PRINT_ONLY_MODULE)) {
        return project.property(Api.Parameters.PRINT_ONLY_MODULE) as String?
      } else {
        return null
      }
    }
  }
}
