package com.jraska.module.graph.assertion

import com.jraska.module.graph.GraphvizWriter
import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class GradleDependencyGraphFactoryTest {

  private val EXPECTED_SINGLE_MODULE_RELATIONS = listOf("\":core\"")

  private val EXPECTED_GRAPHVIZ_RELATIONS = listOf(
    "\":app\" -> \":lib\"",
    "\":app\" -> \":feature\" [color=red style=bold]",
    "\":lib\" -> \":core\" [color=red style=bold]",
    "\":feature\" -> \":core\"",
    "\":feature\" -> \":lib\" [color=red style=bold]",
  )

  private val EXPECTED_TEST_IMPLEMENTATION_RELATIONS = listOf(
    "\":app\" -> \":feature\" [color=red style=bold]",
    "\":feature\" -> \":lib\" [color=red style=bold]",
    "\":feature\" -> \":core-testing\"",
    "\":lib\" -> \":core\" [color=red style=bold]",
    "\":core-testing\" -> \":core\"",
  )

  private lateinit var dependencyCollectorService: DependencyCollectorService
  private lateinit var appProject: DefaultProject
  private lateinit var coreProject: DefaultProject
  private var rootProject: DefaultProject? = null

  @Before
  fun setUp() {
    rootProject = createProject("root")
    appProject = createProject("app")

    val libProject = createProject("lib")
    appProject.dependencies.add("api", libProject)

    val featureProject = createProject("feature")
    appProject.dependencies.add("implementation", featureProject)
    featureProject.dependencies.add("implementation", libProject)

    coreProject = createProject("core")
    featureProject.dependencies.add("api", coreProject)
    libProject.dependencies.add("implementation", coreProject)

    val coreTestingProject = createProject("core-testing")
    coreTestingProject.dependencies.add("implementation", coreProject)
    featureProject.dependencies.add("testImplementation", coreTestingProject)

    dependencyCollectorService = rootProject!!.registerDependencyCollectorService().get()
  }

  @Test
  fun generatesProperGraph() {
    rootProject!!.collectAndRegisterDependencies(Api.API_IMPLEMENTATION_CONFIGURATIONS)
    val dependencyGraph = GradleDependencyGraphFactory.create(appProject, dependencyCollectorService)

    val graphvizText = GraphvizWriter.toGraphviz(dependencyGraph)

    graphvizText.assertRelations(EXPECTED_GRAPHVIZ_RELATIONS)
  }

  @Test
  fun generatesWithTestImplementatinoGraph() {
    rootProject!!.collectAndRegisterDependencies(setOf("implementation", "testImplementation"))
    val dependencyGraph = GradleDependencyGraphFactory.create(appProject, dependencyCollectorService)

    val graphvizText = GraphvizWriter.toGraphviz(dependencyGraph)

    graphvizText.assertRelations(EXPECTED_TEST_IMPLEMENTATION_RELATIONS)
  }

  @Test
  fun generatesSingleModuleGraphOnNoDependencyModule() {
    rootProject!!.collectAndRegisterDependencies(Api.API_IMPLEMENTATION_CONFIGURATIONS)
    val dependencyGraph = GradleDependencyGraphFactory.create(coreProject, dependencyCollectorService)

    val graphvizText = GraphvizWriter.toGraphviz(dependencyGraph)

    graphvizText.assertRelations(EXPECTED_SINGLE_MODULE_RELATIONS)
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
