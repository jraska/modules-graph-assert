package com.jraska.module.graph.assertion

import com.jraska.module.graph.writer.GraphvizWriter
import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class GradleDependencyGraphFactorySingleModuleProjectTest {
  companion object {
    private const val EXPECTED_SINGLE_MODULE = """digraph G {
":app"
}"""

    private const val EXPECTED_ROOT_MODULE = """digraph G {
"root  some-root"
}"""
  }

  private lateinit var singleModule: DefaultProject
  private var rootProject: DefaultProject? = null

  @Before
  fun setUp() {
    rootProject = createProject("some-root")
    singleModule = createProject("app")
  }

  @Test
  fun generatesProperGraph() {
    val dependencyGraph = GradleDependencyGraphFactory.create(singleModule, Api.API_IMPLEMENTATION_CONFIGURATIONS)

    val graphvizText = GraphvizWriter.toGraph(dependencyGraph)
    assert(EXPECTED_SINGLE_MODULE == graphvizText)
  }

  @Test
  fun generatesProperGraphOnRootModule() {
    val dependencyGraph = GradleDependencyGraphFactory.create(rootProject!!, Api.API_IMPLEMENTATION_CONFIGURATIONS)

    val graphvizText = GraphvizWriter.toGraph(dependencyGraph)
    assert(EXPECTED_ROOT_MODULE == graphvizText)
  }

  private fun createProject(name: String): DefaultProject {
    val project =
      ProjectBuilder.builder()
        .withName(name)
        .withParent(rootProject)
        .build() as DefaultProject

    project.plugins.apply(JavaLibraryPlugin::class.java)
    return project
  }
}
