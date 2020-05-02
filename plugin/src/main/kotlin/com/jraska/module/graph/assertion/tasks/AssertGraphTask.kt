package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.assertion.GradleDependencyGraphFactory
import com.jraska.module.graph.assertion.GraphAssert
import com.jraska.module.graph.assertion.UserDefinedRulesAssert
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class AssertGraphTask : DefaultTask() {

  @Input
  lateinit var assertion: GraphAssert

  @Input
  lateinit var configurationsToLook: Set<String>

  @TaskAction
  fun run() {
    val modulesTree = GradleDependencyGraphFactory.create(project, configurationsToLook)

    assertion.assert(modulesTree)
  }
}
