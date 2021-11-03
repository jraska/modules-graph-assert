package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyMatcher
import com.jraska.module.graph.ModuleNameRegexMatcher
import com.jraska.module.graph.Parse
import com.jraska.module.graph.assertion.Api.Tasks
import com.jraska.module.graph.assertion.tasks.AssertGraphTask
import com.jraska.module.graph.assertion.tasks.GenerateModulesGraphStatisticsTask
import com.jraska.module.graph.assertion.tasks.GenerateModulesGraphTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP
import java.util.Locale
import java.util.function.Predicate

@Suppress("unused", "UnstableApiUsage") // Used as plugin
class ModuleGraphAssertionsPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val graphRules = project.extensions.create(GraphRulesExtension::class.java, Api.EXTENSION_ROOT, GraphRulesExtension::class.java)

    project.afterEvaluate {
      addModulesAssertions(project, graphRules)
    }
  }

  internal fun addModulesAssertions(project: Project, graphRules: GraphRulesExtension) {
    project.addModuleGraphGeneration(graphRules)
    project.addModuleGraphStatisticsGeneration(graphRules)

    val allAssertionsTask = project.tasks.register(Tasks.ASSERT_ALL) { it.group = VERIFICATION_GROUP }

    try {
      project.tasks.named(CHECK_TASK_NAME).configure { it.dependsOn(allAssertionsTask) }
    } catch (checkNotFound: UnknownTaskException) {
      // We register other tasks, but we don't add a dependency to 'check' task
    }

    val childTasks = mutableListOf<TaskProvider<AssertGraphTask>>()
    project.addMaxHeightTask(graphRules)?.also { childTasks.add(it) }
    project.addModuleLayersTask(graphRules)?.also { childTasks.add(it) }
    project.addModuleUserRuleTask(graphRules)?.also { childTasks.add(it) }
    project.addModuleAllowedRulesTask(graphRules)?.also { childTasks.add(it) }

    allAssertionsTask.configure { allTask ->
      childTasks.forEach {
        allTask.dependsOn(it)
      }
    }
  }

  private fun Project.addModuleGraphGeneration(graphRules: GraphRulesExtension) {
    tasks.register(Tasks.GENERATE_GRAPHVIZ, GenerateModulesGraphTask::class.java) {
      it.configurationsToLook = graphRules.configurations
    }
  }

  private fun Project.addModuleGraphStatisticsGeneration(graphRules: GraphRulesExtension) {
    tasks.register(Tasks.GENERATE_GRAPH_STATISTICS, GenerateModulesGraphStatisticsTask::class.java) {
      it.configurationsToLook = graphRules.configurations
    }
  }

  private fun Project.addMaxHeightTask(graphRules: GraphRulesExtension): TaskProvider<AssertGraphTask>? {
    if (graphRules.maxHeight <= 0) {
      return null
    }

    val task = tasks.register(Tasks.ASSERT_MAX_HEIGHT, AssertGraphTask::class.java) {
      it.assertion = ModuleTreeHeightAssert(moduleNameForHeightAssert(), graphRules.maxHeight)
      it.configurationsToLook = graphRules.configurations
      it.group = VERIFICATION_GROUP
    }

    return task
  }

  @Deprecated("Will be removed with version 2.0")
  private fun Project.addModuleLayersTask(graphRules: GraphRulesExtension): TaskProvider<AssertGraphTask>? {
    if (graphRules.moduleLayers.isEmpty()) {
      return null
    }

    val task = tasks.register(Tasks.ASSERT_LAYER_ORDER, AssertGraphTask::class.java) {
      println(
        "*Deprecation*: '${Tasks.ASSERT_LAYER_ORDER}' task is deprecated and will be removed in version 2.0. \n" +
          "Please use '${Tasks.ASSERT_ALLOWED}' task instead."
      )

      it.assertion = LayersOrderAssert(graphRules.layerMatchers(), graphRules.excludedFromLayers())
      it.configurationsToLook = graphRules.configurations
      it.group = VERIFICATION_GROUP
    }

    return task
  }

  private fun Project.addModuleUserRuleTask(graphRules: GraphRulesExtension): TaskProvider<AssertGraphTask>? {
    if (graphRules.restricted.isEmpty()) {
      return null
    }

    val task = tasks.register(Tasks.ASSERT_USER_RULES, AssertGraphTask::class.java) {
      it.assertion = UserDefinedRulesAssert(graphRules.userRulesMatchers())
      it.configurationsToLook = graphRules.configurations
      it.group = VERIFICATION_GROUP
    }

    return task
  }

  private fun Project.addModuleAllowedRulesTask(graphRules: GraphRulesExtension): TaskProvider<AssertGraphTask>? {
    if (graphRules.allowed.isEmpty()) {
      return null
    }

    val task = tasks.register(Tasks.ASSERT_ALLOWED, AssertGraphTask::class.java) {
      println(
        "*Notice*: 'allowed' property is experimental, unstable and open for feedback - https://github.com/jraska/modules-graph-assert/issues/129. You can expect final API in version 2.0."
      )

      it.assertion = OnlyAllowedAssert(graphRules.allowedRulesMatchers())
      it.configurationsToLook = graphRules.configurations
      it.group = VERIFICATION_GROUP
    }

    return task
  }

  private fun String.capitalizeFirst(): String {
    return this.substring(0, 1).uppercase(Locale.US).plus(this.substring(1))
  }

  private fun GraphRulesExtension.excludedFromLayers(): Collection<DependencyMatcher> {
    return moduleLayersExclude.map { Parse.matcher(it) }
  }

  private fun GraphRulesExtension.allowedRulesMatchers(): Collection<DependencyMatcher> {
    return allowed.map { Parse.matcher(it) }
  }

  private fun GraphRulesExtension.userRulesMatchers(): Collection<DependencyMatcher> {
    return restricted.map { Parse.restrictiveMatcher(it) }
  }

  private fun GraphRulesExtension.layerMatchers(): Array<Predicate<String>> {
    return moduleLayers.map { ModuleNameRegexMatcher(it.toRegex()) }.toTypedArray()
  }
}

private fun Project.moduleNameForHeightAssert(): String? {
  if (this == rootProject) {
    return null
  } else {
    return moduleDisplayName()
  }
}

fun Project.moduleDisplayName(): String {
  return displayName.replace("project", "")
    .replace("'", "")
    .trim()
}
