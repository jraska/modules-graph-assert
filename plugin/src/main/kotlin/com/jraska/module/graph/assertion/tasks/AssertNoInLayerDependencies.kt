package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.assertion.GradleDependencyGraphFactory
import com.jraska.module.graph.assertion.NoInLayerDependencyAssert
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class AssertNoInLayerDependencies : DefaultTask() {
  @Input
  lateinit var layerPrefix: String

  @Input
  lateinit var excludedForCheck: Set<Pair<String, String>>

  @TaskAction
  fun run() {
    val modulesGraph = GradleDependencyGraphFactory.create(project)

    NoInLayerDependencyAssert(layerPrefix, excludedForCheck).assert(modulesGraph)
  }
}
