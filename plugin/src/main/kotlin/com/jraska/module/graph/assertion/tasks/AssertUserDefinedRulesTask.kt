package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.DependencyMatcher
import com.jraska.module.graph.assertion.GradleDependencyGraphFactory
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class AssertUserDefinedRulesTask : DefaultTask() {
  @Input
  lateinit var matchers: Collection<DependencyMatcher>

  @TaskAction
  fun run() {
    val modulesTree = GradleDependencyGraphFactory.create(project)

    val failedDependencies = modulesTree.dependencyPairs()
      .flatMap { dependency -> matchers.filter { it.matches(dependency) }.map { dependency to it } }

    if(failedDependencies.isNotEmpty()) {
      throw GradleException("Viloated") //todo
    }
  }
}
