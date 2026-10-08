# Fan Concept Toolkit

实在懒得写名字，就是单纯看到风扇的时候临时想的名字，随便吧。

具体库已经上传到中央仓库了，可以自行下载使用，

就是一个参照了`swing`做的demo项目。

`fct`-`1.0.3`

`fct-util`-`1.0.5`

`fct-lwjgl`-`1.0.2`

`fct-platform`-`1.0.2`

```xml
<dependency>
    <groupId>io.github.juicefries</groupId>
    <artifactId>fct-{module}</artifactId>
    <version>{version}</version>     
    <scope>provided</scope>
</dependency>
```
```groovy
// Groovy DSL
dependencies {
    implementation 'io.github.juicefries:fct-{module}:version'
}
```

```kotlin
// Kotlin DSL
dependencies {
    implementation("io.github.juicefries:fct-{module}:{version}")
}
```

data - 2026/10/8-17:39