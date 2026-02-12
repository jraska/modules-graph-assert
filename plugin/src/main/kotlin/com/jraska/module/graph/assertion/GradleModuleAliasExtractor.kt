package com.jraska.module.graph.assertion

import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension

object GradleModuleAliasExtractor {
  fun extractModuleAliases(service: DependencyCollectorService): Map<String, String> =
    service.getAllProjectAliases()

  fun Project.extractOwnAlias(): String? =
    try {
      extensions
        .extraProperties
        .get(Api.Properties.MODULE_NAME_ALIAS) as? String
    } catch (_: ExtraPropertiesExtension.UnknownPropertyException) {
      null
    }
}
