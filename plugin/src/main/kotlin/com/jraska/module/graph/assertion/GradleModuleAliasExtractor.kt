package com.jraska.module.graph.assertion

import org.gradle.api.Project

object GradleModuleAliasExtractor {
  fun extractModuleAliases(project: Project): Map<String, String> {
    return project.allprojects
      .mapNotNull { alias(it) }
      .toMap()
  }

  private fun alias(project: Project): Pair<String, String>? {
    if (project.hasProperty(Api.Properties.MODULE_NAME_ALIAS)) {
      val moduleAlias = project.property(Api.Properties.MODULE_NAME_ALIAS) as String
      return project.moduleDisplayName() to moduleAlias
    } else {
      return null
    }
  }
}
