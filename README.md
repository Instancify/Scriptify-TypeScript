# Scriptify TypeScript
Adds TypeScript support to Scriptify. Uses ready-made implementations of [script-js-graalvm](https://github.com/Instancify/Scriptify/tree/master/script-js-graalvm) and [script-js-rhino](https://github.com/Instancify/Scriptify/tree/master/script-js-rhino), and transcompiles TypeScript using [swc4j](https://github.com/caoccao/swc4j) for evaluation.

## TAKE NOTE
> [!WARNING]
> To run the SWC transpiler, you need to add a dependency for the needed operating system.
### Example:
```xml
<dependency>
    <groupId>com.caoccao.javet</groupId>
    <artifactId>swc4j-linux-x86_64</artifactId>
    <version>1.6.0</version>
</dependency>
```
```groovy
implementation 'com.caoccao.javet:swc4j-windows-x86_64:1.6.0'
```

List of all dependencies:
**https://github.com/caoccao/swc4j?tab=readme-ov-file#dependency**

## Maven
Adding repo:
```xml
<repositories>
    <repository>
        <id>instancify-repository-snapshots</id>
        <url>https://repo.instancify.app/snapshots</url>
    </repository>
</repositories>
```

For adding a library for GraalVM (SWC):
```xml
<dependency>
    <groupId>com.instancify.scriptify.ts</groupId>
    <artifactId>script-ts-swc-graalvm</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

For adding a library for Rhino (SWC):
```xml
<dependency>
    <groupId>com.instancify.scriptify.ts</groupId>
    <artifactId>script-ts-swc-rhino</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Gradle
Adding repo:
```groovy
maven {
    name "instancifyRepositorySnapshots"
    url "https://repo.instancify.app/snapshots"
}
```

For adding a library for GraalVM (SWC):
```groovy
implementation "com.instancify.scriptify.ts:script-ts-swc-graalvm:1.0.0-SNAPSHOT"
```

For adding a library for Rhino (SWC):
```groovy
implementation "com.instancify.scriptify.ts:script-ts-swc-rhino:1.0.0-SNAPSHOT"
```