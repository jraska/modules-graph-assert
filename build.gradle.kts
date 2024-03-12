import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version libs.versions.kotlin.get()
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktlint)
}

kotlin {
  jvmToolchain(libs.versions.java.get().toInt())
}

subprojects {
  apply(plugin = rootProject.libs.plugins.ktlint.get().pluginId)
  apply(plugin = rootProject.libs.plugins.detekt.get().pluginId)

  tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = libs.versions.java.get()
  }

  configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set(rootProject.libs.versions.ktlint.get())
    verbose.set(true)
    filter {
      exclude("**/generated/**", "**/build/**")
      include("src//kotlin")
    }
  }

  detekt {
    buildUponDefaultConfig = true
    parallel = true
  }
}

// tasks.register("clean", Delete::class) {
//  delete(rootProject.layout.buildDirectory)
// }
