# Module Graph Assert
Gradle plugin to keep your modules graph healthy and lean.

[![CircleCI](https://circleci.com/gh/jraska/modules-graph-assert.svg?style=svg)](https://circleci.com/gh/jraska/modules-graph-assert)
[![Gradle Pugin](https://img.shields.io/badge/Gradle-Plugin-green)](https://plugins.gradle.org/plugin/com.jraska.module.graph.assertion)

<img width="1281" alt="example_graph" src="https://user-images.githubusercontent.com/6277721/70832705-18980e00-1df6-11ea-8b78-fc07ba570a2b.png">

## Why modules dependency structure matters
- Modules structure highly affects build speeds.
- Modules separate logical units and enforce proper dependencies.
- Module graph can silently degenerate into structure similar to list.
- Breaking problematic module dependencies can be very difficult, it is cheaper to prevent them.
- Murphy's law of dependencies: "Whetever they can access, they will access." - If not enforced, undesirable module dependencies will appear.

## What we can enforce
- This plugin allows simple way how to define rules, which will be verified with task `assertModulesGraph` as part of `check` Gradle task.
- **Regex** on module names is used for matching **module names**.
- `moduleLayers = [":feature:\\S*", ":lib\\S*", ":core\\S*"]` can define order of layers from the top. 
Modules cannot be dependent within layer and dependencies cannot go against the direction of layers. Any module with `:feature:` prefix cannot depend on other with `:feature:` prefix. `:lib` prefixed module cannot depend on `:feature:` etc. If there are allowed exceptions you can use `moduleLayersExclude = [":feature-about -> :feature-legacy-about"]`
- `restricted [':feature-[a-z]* -X> :forbidden-to-depend-on']` helps us to define custom rules by using `regex -X> regex` signature.
- `maxHeight = 4` Can verify that [height of modules tree](https://stackoverflow.com/questions/2603692/what-is-the-difference-between-tree-depth-and-height) with a root in the module will not exceed 4. Tree height is a good metric to prevent module tree degeneration into list. 

## Usage
```groovy
plugins {
  id "com.jraska.module.graph.assertion" version "0.4.1"
}
```

### Configuration
- Rules are applied on the applied Gradle module and its dependencies. Typically you would like to apply this in your final `:app` module, however configuration for any module is possible. [Example](https://github.com/jraska/github-client/blob/master/app/build.gradle#L141)

```groovy
moduleGraphAssert {
  maxHeight = 4
  moduleLayers = [":feature:\\S*", ":lib\\S*", ":core\\S*"] // modules prefixed with ":feature:" -> prefix ":lib:" -> prefix :core:
  moduleLayersExclude = [":feature-about -> :feature-legacy-about"]
  restricted = [':feature-[a-z]* -X> :forbidden-to-depend-on'] //regex to match module names
}
```

### Helper Graphviz Graph Export
- Visualising graph could be useful to see the dependency problem, therefore helper `generateModulesGraphvizText` is included.
- By default it generates graph of all modules in a project.
- Longest path of the project is in red.
- Adding parameter `modules.graph.print.statistics` prints also information about the graph.
- You can set `modules.graph.of.module` parameter if you want only sub-graph of total graph.
```
./gradlew generateModulesGraphvizText -Pmodules.graph.print.statistics=true -Pmodules.graph.of.module=:feature-one
```
- Adding parameter `modules.graph.output.gv` saves the graphViz file to specified path
```
./gradlew generateModulesGraphvizText -Pmodules.graph.output.gv=all_modules
```
