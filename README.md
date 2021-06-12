# Module Graph Assert
A Gradle plugin that helps keep your module graph healthy and lean.

[Medium Article](https://proandroiddev.com/module-rules-protect-your-build-time-and-architecture-d1194c7cc6bc) with complete context.

[![Build](https://github.com/jraska/modules-graph-assert/actions/workflows/build.yml/badge.svg)](https://github.com/jraska/modules-graph-assert/actions/workflows/build.yml)
[![Gradle Pugin](https://img.shields.io/badge/Gradle-Plugin-green)](https://plugins.gradle.org/plugin/com.jraska.module.graph.assertion)

<img width="1281" alt="example_graph" src="https://user-images.githubusercontent.com/6277721/70832705-18980e00-1df6-11ea-8b78-fc07ba570a2b.png">

## Why the module dependency structure matters
- Build speeds can be very dependent on the structure of your module graph.
- Modules separate logical units and enforce proper dependencies.
- The module graph can silently degenerate into a list-like structure.
- Breaking problematic module dependencies can be very difficult, it is cheaper to prevent them.
- If not enforced, undesirable module dependencies will appear. Murphy's law of dependencies: "Whatever they can access, they will access."

## What we can enforce
- The plugin provides a simple way for defining rules, which will be verified with the task `assertModuleGraph` as part of the `check` task.
- Match module names using regular expressions.
- Define the order of layers from the top.
  - `moduleLayers = [":feature:\\S*", ":lib\\S*", ":core\\S*"]`
  - Modules cannot be dependent within a layer and dependencies cannot go against the direction of the layers. 
  - Any module with `:feature:` prefix cannot depend on another with `:feature:` prefix. 
  - `:lib` prefixed module cannot depend on a `:feature:` etc. 
  - If there are allowed exceptions you can use `moduleLayersExclude = [":feature-about -> :feature-legacy-about"]`
- `restricted [':feature-[a-z]* -X> :forbidden-to-depend-on']` helps us to define custom rules by using `regex -X> regex` signature.
- `maxHeight = 4` can verify that the [height of the modules tree](https://stackoverflow.com/questions/2603692/what-is-the-difference-between-tree-depth-and-height) with a root in the module will not exceed 4. Tree height is a good metric to prevent module tree degeneration into a list. 

## Usage
Apply the plugin to a module, which dependencies graph you want to assert.
```groovy
plugins {
  id "com.jraska.module.graph.assertion" version "1.5.1"
}
```

You can run `./gradlew assertModuleGraph` to execute configured checks or `./gradlew check` where `assertModuleGraph` will be included.

### Configuration
Rules are applied on the Gradle module and its `api` and `implementation` dependencies by default. Typically you would want to apply this in your final app module, however configuration for any module is possible. [Example](https://github.com/jraska/github-client/blob/master/app/build.gradle#L141)

```groovy
moduleGraphAssert {
  maxHeight = 4
  moduleLayers = [":feature:\\S*", ":lib\\S*", ":core\\S*"] // modules prefixed with ":feature:" -> prefix ":lib:" -> prefix :core:
  moduleLayersExclude = [":feature-about -> :feature-legacy-about"]
  restricted = [':feature-[a-z]* -X> :forbidden-to-depend-on'] // regex to match module names
  configurations = ['api', 'implementation'] // Dependency configurations to look. ['api', 'implementation'] is the default
}
```

### Graphviz Graph Export
- Visualising the graph could be useful to help find your dependency issues, therefore a helper `generateModulesGraphvizText` task is included.
- This generates a graph of dependent modules when the plugin is applied.
- The longest path of the project is in red.
- If you utilise [Configuration on demand](https://docs.gradle.org/current/userguide/multi_project_builds.html#sec:configuration_on_demand) Gradle feature, please use `--no-configure-on-demand` flag along the `generateModulesGraphvizText` task.
- You can set the `modules.graph.of.module` parameter if you are only interested in a sub-graph of the module graph.
```
./gradlew generateModulesGraphvizText -Pmodules.graph.of.module=:feature-one
```
- Adding the parameter `modules.graph.output.gv` saves the graphViz file to the specified path
```
./gradlew generateModulesGraphvizText -Pmodules.graph.output.gv=all_modules
```

### Graph statistics
- Executing the task `generateModulesGraphStatistics` prints the information about the graph.
- Statistics printed: Modules Count, [Edges Count](https://en.wikipedia.org/wiki/Glossary_of_graph_theory_terms#edge), [Height](https://en.wikipedia.org/wiki/Glossary_of_graph_theory_terms#height) and [Longest Path](https://en.wikipedia.org/wiki/Longest_path_problem) 
- Parameter `-Pmodules.graph.of.module` is supported as well.
```
./gradlew generateModulesGraphStatistics -Pmodules.graph.of.module=:feature-one
```
