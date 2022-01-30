package com.jraska.module.graph.assertion

import com.google.common.truth.Truth.assertThat
import com.jraska.module.graph.DependencyGraph
import org.gradle.api.GradleException
import org.junit.Assert.assertThrows
import org.junit.Test

class RestrictedDependenciesAssertTest {
  @Test
  fun passesWithNoMatchingMatchers() {
    val dependencyGraph = testGraph()
    RestrictedDependenciesAssert(emptyArray()).assert(dependencyGraph)
  }

  @Test
  fun failsWhenFeatureCannotDependOnLib() {
    // Given
    val dependencyGraph = testGraph()
    val restrictedDependenciesAssert = RestrictedDependenciesAssert(arrayOf("feature -X> lib2"))

    // When
    val thrown = assertThrows(GradleException::class.java) {
      restrictedDependenciesAssert.assert(dependencyGraph)
    }

    // Then
    assertThat(thrown).isInstanceOf(GradleException::class.java)
    assertThat(thrown)
        .hasMessageThat()
        .isEqualTo("Dependency ''feature' -> 'lib2' violates: 'feature -X> lib2'")
  }

  @Test
  fun failsWhenLibCannotDependOnAndroid() {
    // Given
    val dependencyGraph = testGraph()
    val restrictedDependenciesAssert = RestrictedDependenciesAssert(
        arrayOf("lib[0-9]* -X> [a-z]*-android")
    )

    // When
    val thrown = assertThrows(GradleException::class.java) {
      restrictedDependenciesAssert.assert(dependencyGraph)
    }

    // Then
    assertThat(thrown).isInstanceOf(GradleException::class.java)
    assertThat(thrown)
      .hasMessageThat()
      .isEqualTo(
        "Dependency ''lib' -> 'core-android' violates: 'lib[0-9]* -X> [a-z]*-android'\n" +
        "Dependency ''lib2' -> 'core-android' violates: 'lib[0-9]* -X> [a-z]*-android'"
      )
  }

  @Test
  fun logsCustomMessageWhenProvidedOnRestrictedDependencyViolation() {
      // Given
      val dependencyGraph = testGraph()
      val restrictedDependenciesAssert = RestrictedDependenciesAssert(
          errorMatchers = arrayOf("feature -X> lib2"),
          customMessageOnRestrictedFailure = "For more information on dependency violations " +
              "follow the link: https://link.to.dependency.violations.documentation.com"
      )

      // When
      val thrown = assertThrows(GradleException::class.java) {
          restrictedDependenciesAssert.assert(dependencyGraph)
      }

      // Then
      assertThat(thrown).isInstanceOf(GradleException::class.java)
      assertThat(thrown)
          .hasMessageThat()
          .isEqualTo(
              "Dependency ''feature' -> 'lib2' violates: 'feature -X> lib2'\n" +
                  "For more information on dependency violations " +
                  "follow the link: https://link.to.dependency.violations.documentation.com"
          )
  }

  @Test
  fun passesWithNoMatchersToAlias() {
    val dependencyGraph = DependencyGraph.create(
      "app" to "feature",
      "app" to "feature2",
      "feature2" to "core",
      "feature" to "core"
    )

    val aliases = mapOf(
      "app" to "App",
      "feature2" to "Impl",
      "feature" to "Impl",
      "core" to "Api"
    )

    RestrictedDependenciesAssert(
      arrayOf("Api -X> Impl", "Impl -X> Impl"),
      aliases
    ).assert(dependencyGraph)
  }

  @Test(expected = GradleException::class)
  fun failsWithMatchersToAlias() {
    val dependencyGraph = DependencyGraph.create(
      "app" to "feature",
      "app" to "feature2",
      "feature2" to "core",
      "feature" to "core",
      "feature" to "feature2",
    )

    val aliases = mapOf(
      "app" to "App",
      "feature2" to "Impl",
      "feature" to "Impl",
      "core" to "Api"
    )

    RestrictedDependenciesAssert(
      arrayOf("Api -X> Impl", "Impl -X> Impl"),
      aliases
    ).assert(dependencyGraph)
  }

  private fun testGraph(): DependencyGraph {
    return DependencyGraph.create(
      "app" to "feature",
      "app" to "feature2",
      "app" to "lib",
      "feature" to "lib",
      "feature" to "lib2",
      "feature" to "feature2",
      "lib" to "core",
      "lib" to "core-android",
      "lib2" to "core-android",
      "core-android" to "core"
    )
  }
}
