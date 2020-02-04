package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.DependencyMatcher
import com.jraska.module.graph.assertion.GradleDependencyGraphFactory
import com.jraska.module.graph.assertion.UserDefinedRulesAssert
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

    UserDefinedRulesAssert(matchers).assert(modulesTree)
  }
}
