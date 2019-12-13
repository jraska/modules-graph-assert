package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.assertion.GradleDependencyGraphFactory
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class AssertModuleTreeHeightTask : DefaultTask() {
  @Input
  lateinit var moduleName: String

  @Input
  var maxHeight: Int = 0

  @TaskAction
  fun run() {
    val modulesTree = GradleDependencyGraphFactory.create(project)

    val height = modulesTree.heightOf(moduleName)
    if (height > maxHeight) {
      val longestPath = modulesTree.longestPath(moduleName)
      throw GradleException("Module $moduleName is allowed to have maximum height of $maxHeight, but has $height, problematic dependencies: ${longestPath.pathString()}")
    }
  }
}
