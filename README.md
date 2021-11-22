# Module Graph Assert
A Gradle plugin that helps keep your module graph healthy and lean.

- [Medium Article](https://proandroiddev.com/module-rules-protect-your-build-time-and-architecture-d1194c7cc6bc) with complete context.
- [Talk about module graph and build times](https://www.droidcon.com/2021/11/10/nail-your-gradle-build-time/) - *Modularization part starts at 11:39 🕑*
- [Changelog](https://github.com/jraska/modules-graph-assert/releases)

[![Build](https://github.com/jraska/modules-graph-assert/actions/workflows/build.yml/badge.svg)](https://github.com/jraska/modules-graph-assert/actions/workflows/build.yml)
[![Gradle Plugin](https://img.shields.io/badge/Gradle-Plugin-green)](https://plugins.gradle.org/plugin/com.jraska.module.graph.assertion)

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
- Define the only allowed dependencies between modules
  - `allowed = [':feature-one -> :feature-[a-z-:]*', ':.* -> :core', ':feature.* -> :lib.*']` define rules by using `regex -> regex` signature.
  - Dependency, which will not match any of those rules will fail the assertion.
- `restricted [':feature-[a-z]* -X> :forbidden-to-depend-on']` helps us to define custom rules by using `regex -X> regex` signature.
- `maxHeight = 4` can verify that the [height of the modules tree](https://stackoverflow.com/questions/2603692/what-is-the-difference-between-tree-depth-and-height) with a root in the module will not exceed 4. Tree height is a good metric to prevent module tree degeneration into a list.
 
## Usage
Apply the plugin to a module, which dependencies graph you want to assert.
```groovy
plugins {
  id "com.jraska.module.graph.assertion" version "2.0.0"
}
```

You can run `./gradlew assertModuleGraph` to execute configured checks or `./gradlew check` where `assertModuleGraph` will be included.

### Configuration
Rules are applied on the Gradle module and its `api` and `implementation` dependencies by default. Typically you would want to apply this in your final app module, however configuration for any module is possible. [Example](https://github.com/jraska/github-client/blob/master/app/build.gradle#L141)

```groovy
moduleGraphAssert {
  maxHeight = 4
  allowed = [':.* -> :core', ':feature.* -> :lib.*'] // regex to match module names
  restricted = [':feature-[a-z]* -X> :forbidden-to-depend-on'] // regex to match module names
  configurations = ['api', 'implementation'] // Dependency configurations to look. ['api', 'implementation'] is the default
}
```

### Module name alias
- You don't have to rely on module names and set a property `ext.moduleNameAssertAlias = "ThisWillBeAssertedOn"`
- This can be set on any module and the `allowed`/`restricted` rules would use the alias instead of module name for asserting.
- This is useful for example if you want to use "module types" where eaach module has a type regardless the name and you want to manage only dependnecies of different types.
- It is recommended to use either module names or `moduleNameAssertAlias` everywhere. Mixing both is not recommended.
- Example of module rules you could implement for a flat module graph:

<img src="https://user-images.githubusercontent.com/6277721/142781792-752f39ce-1525-4f59-8a25-94b236476117.png" width="300" />`
  - Each module would have set `ext.moduleNameAssertAlias = "Api|Implementation|App"`
  - Module rules example for such case: `allowed = ['Implementation -> Api', 'App -> .*']`
  - In case you want to migrate to this structure incrementally, you can set a separate module type like `ext.moduleNameAssertAlias = "NeedsMigration"` and setting `allowed = ['Implementation -> Api', 'App -> .*', 'NeedsToMigrate -> .*', '.* -> NeedsToMigrate']` and then tackling `"NeedsToMigrate"` modules one by one.

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

## Contributing

Please feel free to create PR or issue with any suggestions or ideas. No special format required, just common sense.

### Debugging

**Setting up a composite build:**

This case is helpful when you need to debug in a real project.
Composite builds are consumed directly without publishing a snapshot version.  
This is done already in `example` project, but you can do the same in any other project:

settings.gradle:
```groovy
includeBuild("path/to/modules-graph-assert")
```

Root build.gradle:
```groovy
plugins {
  id('com.jraska.module.graph.assertion')
}
```
