package com.jraska.module.graph.assertion

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FullProjectGradleTest {
  @get:Rule
  val testProjectDir: TemporaryFolder = TemporaryFolder()

  @Before
  fun setup() {
    testProjectDir.newFile("settings.gradle").writeText("include ':app', ':core', ':feature', 'core-api'")

    createModule(
      "core-api",
      content = """
      apply plugin: 'java-library'
      
      ext.moduleNameAssertAlias = "Api"
      """,
    )

    createModule(
      "core",
      content = """
      apply plugin: 'java-library'

      dependencies {
        implementation project(":core-api")
      }
      ext.moduleNameAssertAlias = "Implementation"
      """,
    )

    createModule(
      "feature",
      content = """
      apply plugin: 'java-library'

      dependencies {
        implementation project(":core-api")
      }
      
      ext.moduleNameAssertAlias = "Implementation"
      """,
    )

    createModule(
      "app",
      content = """
          plugins {
              id 'com.jraska.module.graph.assertion'
          }
          apply plugin: 'java-library'
          
          moduleGraphAssert {
            maxHeight = 2
            allowed = ['Implementation -> Api', 'App -> .*']
            restricted = ['Api -X> Api', 'Implementation -X> Implementation']
          }
          
          dependencies {
            implementation project(":core-api")
            implementation project(":core")
            implementation project(":feature")
          }
     
          ext.moduleNameAssertAlias = "App"
      """,
    )
  }

  @Test
  fun supportsConfigurationCache() {
    runGradleAssertModuleGraph(testProjectDir.root)
    val secondRunResult = runGradleAssertModuleGraph(testProjectDir.root)

    assert(secondRunResult.output.contains("Reusing configuration cache."))
  }

  @Test
  fun statisticsSupportConfigurationCache() {
    runGradleAssertModuleGraph(testProjectDir.root, "--configuration-cache", "generateModulesGraphStatistics")
    val secondRunResult =
      runGradleAssertModuleGraph(
        testProjectDir.root,
        "--configuration-cache",
        "generateModulesGraphStatistics",
      )

    assert(secondRunResult.output.contains("Reusing configuration cache."))
  }

  @Test
  fun moduleGraphSupportConfigurationCache() {
    runGradleAssertModuleGraph(testProjectDir.root, "--configuration-cache", "generateModulesGraphvizText")
    val secondRunResult =
      runGradleAssertModuleGraph(
        testProjectDir.root,
        "--configuration-cache",
        "generateModulesGraphvizText",
      )

    assert(secondRunResult.output.contains("Reusing configuration cache."))
  }

  @Test
  fun printsCorrectStatistics() {
    val output = runGradleAssertModuleGraph(testProjectDir.root, "generateModulesGraphStatistics").output

    assert(
      output.contains(
        "GraphStatistics(modulesCount=4, edgesCount=5, height=2, longestPath=':app -> :core -> :core-api')",
      ),
    )
  }

  @Test
  fun printsOnlyModule() {
    val output =
      runGradleAssertModuleGraph(
        testProjectDir.root,
        "generateModulesGraphvizText",
        "-Pmodules.graph.of.module=:feature",
      ).output

    MatcherAssert.assertThat(
      output,
      CoreMatchers.containsString(
        "digraph G {\n" +
          "\":feature('Implementation')\" -> \":core-api('Api')\" [color=red style=bold]\n" +
          "}",
      ),
    )
  }

  @Test
  fun printsOnlyModuleStatistics() {
    val output =
      runGradleAssertModuleGraph(
        testProjectDir.root,
        "generateModulesGraphStatistics",
        "-Pmodules.graph.of.module=:feature",
      ).output

    MatcherAssert.assertThat(
      output,
      CoreMatchers.containsString(
        "GraphStatistics(modulesCount=2, edgesCount=1, height=1, longestPath=':feature -> :core-api')",
      ),
    )
  }

  @Test
  fun savesGraphIntoFile() {
    val outputFile = File(testProjectDir.root, "all_modules.dot")
    val output =
      runGradleAssertModuleGraph(
        testProjectDir.root,
        "generateModulesGraphvizText",
        "-Pmodules.graph.output.gv=${outputFile.absolutePath}",
      ).output

    assert(output.contains("Graph saved to"))
    assert(outputFile.readText() == EXPECTED_GRAPHVIZ_TEXT)
  }

  private fun createModule(
    dir: String,
    content: String,
  ) {
    val newFolder = testProjectDir.newFolder(dir)
    File(newFolder, "build.gradle").writeText(content)
  }

  companion object {
    const val EXPECTED_GRAPHVIZ_TEXT = """digraph G {
":app('App')" -> ":core-api('Api')"
":app('App')" -> ":core('Implementation')" [color=red style=bold]
":app('App')" -> ":feature('Implementation')"
":core('Implementation')" -> ":core-api('Api')" [color=red style=bold]
":feature('Implementation')" -> ":core-api('Api')"
}"""
  }
}
