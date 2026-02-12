package com.jraska.module.graph.assertion

import com.jraska.module.graph.assertion.ModuleGraphAssertionsSettingsPlugin.Companion.SERVICE_NAME
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.provider.Provider

fun Project.registerDependencyCollectorService(
  configurations: Set<String> = Api.API_IMPLEMENTATION_CONFIGURATIONS,
): Provider<DependencyCollectorService> =
  gradle.registerDependencyCollectorService(configurations)

fun Settings.registerDependencyCollectorService(
  configurations: Set<String> = Api.API_IMPLEMENTATION_CONFIGURATIONS,
): Provider<DependencyCollectorService> =
  gradle.registerDependencyCollectorService(configurations)

private fun Gradle.registerDependencyCollectorService(
  configurations: Set<String>,
): Provider<DependencyCollectorService> =
  sharedServices
    .registerIfAbsent(SERVICE_NAME, DependencyCollectorService::class.java) {
      it.parameters.configurations.set(configurations)
    }

fun Project.getDependencyCollectorService(): DependencyCollectorService? =
  gradle
    .sharedServices
    .registrations
    .findByName(SERVICE_NAME)
    ?.service
    ?.get() as? DependencyCollectorService
