package com.jraska.module.graph.assertion

import com.jraska.module.graph.GraphvizWriter
import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class GradleDependencyGraphFactoryTest {

  private val EXPECTED_GRAPHVIZ = """digraph G {
":app" -> ":lib"
":app" -> ":feature" [color=red style=bold]
":lib" -> ":core" [color=red style=bold]
":feature" -> ":lib" [color=red style=bold]
":feature" -> ":core"
":core-testing" -> ":core"
}"""

  private lateinit var appProject: DefaultProject
  private var rootProject: DefaultProject? = null

  @Before
  fun setUp() {
    rootProject = createProject("root")
    appProject = createProject("app")

    val libProject = createProject("lib")
    appProject.dependencies.add("api", libProject)

    val featureProject = createProject("feature")
    appProject.dependencies.add("implementation", featureProject)
    featureProject.dependencies.add("api", libProject)

    val coreProject = createProject("core")
    featureProject.dependencies.add("api", coreProject)
    libProject.dependencies.add("implementation", coreProject)

    val coreTestingProject = createProject("core-testing")
    coreTestingProject.dependencies.add("implementation", coreProject)
    featureProject.dependencies.add("testImplementation", coreTestingProject)
  }

  @Test
  fun generatesProperGraph() {
    val dependencyGraph = GradleDependencyGraphFactory.create(appProject)

    val graphvizText = GraphvizWriter.toGraphviz(dependencyGraph)
    assert(EXPECTED_GRAPHVIZ == graphvizText)
  }

  private fun createProject(name: String): DefaultProject {
    val project = ProjectBuilder.builder()
      .withName(name)
      .withParent(rootProject)
      .build() as DefaultProject

    project.plugins.apply(JavaLibraryPlugin::class.java)
    return project
  }
}
