package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.assertion.GenerateModulesGraph
import com.jraska.module.graph.assertion.OutputFormat
import org.gradle.api.DefaultTask
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

  @Input
  lateinit var dependencyGraph: DependencyGraph.SerializableGraph

  @Optional
  @Input
  var outputFilePath: String? = null

  @Optional
  @OutputFile
  var outputFile: File? = null

  @Optional
  @Input
  var outputFormat: OutputFormat = OutputFormat.GRAPHVIZ

  @TaskAction
  fun run() {
    GenerateModulesGraph(
      aliases = aliases,
      onlyModuleToPrint = onlyModuleToPrint,
      dependencyGraph = dependencyGraph,
      outputFilePath = outputFilePath,
      outputFile = outputFile,
      outputFormat = outputFormat,
    ).run(path)
  }
}
