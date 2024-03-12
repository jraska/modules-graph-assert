package com.jraska.module.graph.assertion

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FullProjectRootGradleTest {
  @get:Rule
  val testProjectDir: TemporaryFolder = TemporaryFolder()

  @Test
  fun printsCorrectStatisticsForRootProjectWithDependency() {
    testProjectDir.newFile("settings.gradle")
      .writeText("include ':core'")

    createRoot(
      content =
        """
        plugins {
            id 'com.jraska.module.graph.assertion'
        }
        apply plugin: 'java-library'
        
        moduleGraphAssert {
          maxHeight = 1
        }
        dependencies {
          implementation project(":core")
        }
        """.trimIndent(),
    )

    createModule(
      "core",
      content = """
      apply plugin: 'java-library'
      """,
    )

    val output =
      runGradleAssertModuleGraph(testProjectDir.root, "generateModulesGraphStatistics").output

    assert(
      output.contains(
        (
          "> Task :generateModulesGraphStatistics\n.*" +
            "GraphStatistics\\(modulesCount=2, edgesCount=1, height=1, longestPath=\'root.* -> :core\'\\)"
        ).toRegex(),
      ),
    )
  }

  @Test
  fun printsCorrectStatisticsForIndependentRootProject() {
    testProjectDir.newFile("settings.gradle")
      .writeText("include ':app'")

    createRoot(
      content =
        """
        plugins {
            id 'com.jraska.module.graph.assertion'
        }
        apply plugin: 'java-library'
        
        moduleGraphAssert {
          maxHeight = 0
        }
        """.trimIndent(),
    )

    createModule(
      "app",
      content = """
      plugins {
          id 'com.jraska.module.graph.assertion'
      }
      apply plugin: 'java-library'
      moduleGraphAssert {
        maxHeight = 0
      }
      """,
    )

    val output =
      runGradleAssertModuleGraph(testProjectDir.root, "generateModulesGraphStatistics").output

    assert(
      output.contains(
        (
          "> Task :generateModulesGraphStatistics\n.*" +
            "GraphStatistics\\(modulesCount=1, edgesCount=0, height=0, longestPath=\'root.*\'\\)\n\n" +
            "> Task :app:generateModulesGraphStatistics\n+" +
            "GraphStatistics\\(modulesCount=1, edgesCount=0, height=0, longestPath=\':app\'\\)"
        ).toRegex(),
      ),
    )
  }

  private fun createRoot(content: String) {
    File(testProjectDir.root, "build.gradle").writeText(content)
  }

  private fun createModule(
    dir: String,
    content: String,
  ) {
    val newFolder = testProjectDir.newFolder(dir)
    File(newFolder, "build.gradle").writeText(content)
  }
}
