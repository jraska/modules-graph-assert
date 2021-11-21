package com.jraska.module.graph.assertion

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

@Suppress("unused", "UnstableApiUsage") // Used as plugin
class ModuleGraphAssertionsPlugin : Plugin<Project> {

  private val moduleGraph by lazy {
    GradleDependencyGraphFactory.create(evaluatedProject, configrationsToLook).serializableGraph()
  }

  private lateinit var evaluatedProject: Project
  private lateinit var configrationsToLook: Set<String>

  override fun apply(project: Project) {
    val graphRules = project.extensions.create(GraphRulesExtension::class.java, Api.EXTENSION_ROOT, GraphRulesExtension::class.java)

    project.afterEvaluate {
      addModulesAssertions(project, graphRules)
    }
  }

  internal fun addModulesAssertions(project: Project, graphRules: GraphRulesExtension) {
    evaluatedProject = project
    configrationsToLook = graphRules.configurations

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
    project.addModuleRestrictionsTask(graphRules)?.also { childTasks.add(it) }
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

    return tasks.register(Tasks.ASSERT_MAX_HEIGHT, AssertGraphTask::class.java) {
      it.assertion = ModuleTreeHeightAssert(moduleNameForHeightAssert(), graphRules.maxHeight)
      it.dependencyGraph = moduleGraph
      it.outputs.upToDateWhen { true }
      it.group = VERIFICATION_GROUP
    }
  }

  private fun Project.addModuleRestrictionsTask(graphRules: GraphRulesExtension): TaskProvider<AssertGraphTask>? {
    if (graphRules.restricted.isEmpty()) {
      return null
    }

    return tasks.register(Tasks.ASSERT_RESTRICTIONS, AssertGraphTask::class.java) {
      it.assertion = UserDefinedRulesAssert(graphRules.restricted)
      it.dependencyGraph = moduleGraph
      it.outputs.upToDateWhen { true }
      it.group = VERIFICATION_GROUP
    }
  }

  private fun Project.addModuleAllowedRulesTask(graphRules: GraphRulesExtension): TaskProvider<AssertGraphTask>? {
    if (graphRules.allowed.isEmpty()) {
      return null
    }

    return tasks.register(Tasks.ASSERT_ALLOWED, AssertGraphTask::class.java) {
      println(
        "*Notice*: 'allowed' property is experimental, unstable and open for feedback - https://github.com/jraska/modules-graph-assert/issues/129. You can expect final API in version 2.0."
      )

      it.assertion = OnlyAllowedAssert(graphRules.allowed)
      it.dependencyGraph = moduleGraph
      it.outputs.upToDateWhen { true }
      it.group = VERIFICATION_GROUP
    }
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
