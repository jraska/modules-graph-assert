package com.jraska.module.graph.assertion

object Api {
  object Tasks {
    const val GROUP = "module graph assert"
    const val GENERATE_GRAPHVIZ = "generateModulesGraphvizText"
    const val GENERATE_MERMAID = "generateModulesMermaidText"
    const val GENERATE_GRAPH_STATISTICS = "generateModulesGraphStatistics"

    const val ASSERT_ALL = "assertModuleGraph"
    const val ASSERT_MAX_HEIGHT = "assertMaxHeight"
    const val ASSERT_RESTRICTIONS = "assertRestrictions"
    const val ASSERT_ALLOWED = "assertAllowedModuleDependencies"
  }

  object Parameters {
    const val PRINT_ONLY_MODULE = "modules.graph.of.module"
    const val OUTPUT_PATH = "modules.graph.output.gv"
    const val OUTPUT_PATH_MERMAID = "modules.graph.output.mermaid"
  }

  object Properties {
    const val MODULE_NAME_ALIAS = "moduleNameAssertAlias"
  }

  const val EXTENSION_ROOT = "moduleGraphAssert"

  val API_IMPLEMENTATION_CONFIGURATIONS = setOf("api", "implementation")
}
