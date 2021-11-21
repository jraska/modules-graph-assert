package com.jraska.module.graph.assertion

import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ConfigurationAvoidanceTest {
  @get:Rule
  val testProjectDir: TemporaryFolder = TemporaryFolder()

  private lateinit var buildFile: File

  @Before
  fun setup() {
    println(testProjectDir.root)

    buildFile = testProjectDir.newFile("build.gradle")
  }

  @Test
  fun supportsConfigurationCache() {
    buildFile.writeText(
      """
          plugins {
              id 'com.jraska.module.graph.assertion'
          }
          
          moduleGraphAssert {
            maxHeight = 3
            allowed = [':app -> .*', ':feature-\\S* -> :lib\\S*', '.* -> :core', ':feature-one -> :feature-exclusion-test', ':feature-one -> :feature-one:nested']
            restricted = [':feature-[a-z]* -X> :feature-[a-z]*']
       }
      """
    )

    GradleRunner.create()
      .withProjectDir(testProjectDir.root)
      .withPluginClasspath()
      .withArguments("--configuration-cache", "assertModuleGraph")
      .build()

    val result = GradleRunner.create()
      .withProjectDir(testProjectDir.root)
      .withPluginClasspath()
      .withArguments("--configuration-cache", "assertModuleGraph")
      .build()

    println(result.output)
    require(result.output.contains("Reusing configuration cache."))

    val findAll = Regex("UP-TO-DATE").findAll(result.output)
    assert(findAll.count() == 4)
  }
}
