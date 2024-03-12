plugins {
  alias(libs.plugins.pluginPublish)
  `java-gradle-plugin`
}

apply(plugin = "kotlin")

dependencies {
  implementation(gradleApi())
  implementation(libs.kotlin.stdlib)

  implementation(libs.junit)
}

group = "com.jraska.module.graph.assertion"

gradlePlugin {
  website = "https://github.com/jraska/modules-graph-assert"
  vcsUrl = "https://github.com/jraska/modules-graph-assert"

  plugins {
    create("modulesGraphAssert") {
      id = "com.jraska.module.graph.assertion"
      version = "3.0.0"
      displayName = "Modules Graph Assert"
      description = "Gradle plugin to keep your modules graph healthy and lean."
      implementationClass = "com.jraska.module.graph.assertion.ModuleGraphAssertionsPlugin"
      tags.addAll("graph", "assert", "build speed", "android(", ")java", "kotlin", "quality", "multiprojects", "module")
    }
  }
}
