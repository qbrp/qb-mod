import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0"
    id("fabric-loom") version "1.9.1"
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.5"
    kotlin("plugin.serialization") version "2.1.0"
    kotlin("plugin.scripting") version "1.9.10"
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 17
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("engine") {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

repositories {
    mavenCentral()
    maven("https://repo.plo.su")
    maven("https://repo.plasmoverse.com/releases")
    maven("https://repo.plasmoverse.com/snapshots")
    maven("https://maven.enginehub.org/repo/")
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots1"
        mavenContent { snapshotsOnly() }
    }
    maven {
        name = "IzzelAliz Maven"
        url = uri("https://maven.izzel.io/releases/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        name = "Ladysnake Mods"
        url = uri("https://maven.ladysnake.org/releases")
    }
    maven {
        url = uri("https://api.modrinth.com/maven")
    }
    maven {
        name = "FelNull Maven"
        url = uri("https://maven.felnull.dev")
    }

    maven("https://maven.nucleoid.xyz/") { name = "Nucleoid" }
}

val includeDependency by extra(
    System.getenv("INCLUDE_DEPENDENCY")?.toBoolean() ?: false
)
dependencies {
    // Основные зависимости
    implementation(kotlin("reflect"))
    include(implementation("org.reflections:reflections:0.10.2")!!)
    include(implementation("org.javassist:javassist:3.28.0-GA")!!)
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")
    modImplementation("eu.pb4:placeholder-api:${project.property("placeholder_api_version")}")
    if (includeDependency) {
        modImplementation(include("net.kyori:adventure-platform-fabric:${project.property("adventure_version")}")!!)
    }
    include(implementation("net.kyori:adventure-text-serializer-gson:4.14.0")!!)

    // Корутины
    include(implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")!!)
    include(implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")!!)

    // Скрипты
    implementation(kotlin("stdlib"))
    include(implementation("org.jetbrains.kotlin:kotlin-scripting-common")!!)
    include(implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")!!)
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host")
    include(implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies")!!)
    include(implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven")!!)

    //Obj Model Loader
    modApi("dev.felnull:special-model-loader:1.2.0")
    include("dev.felnull:special-model-loader:1.2.0")

    // ModernUI зависимости
    implementation("icyllis.modernui:ModernUI-Core:${project.property("modernui_core_version")}")
    implementation("icyllis.modernui:ModernUI-Markdown:${project.property("modernui_markdown_version")}")
    modImplementation("icyllis.modernui:ModernUI-Fabric:1.20.1-${project.property("modernui_version")}")

    // PlasmoVoice API и дополнительные библиотеки
    implementation("su.plo.voice.api:server:${project.property("voice_api_version")}")
    compileOnly("su.plo.voice.api:client:${project.property("voice_api_version")}")
    implementation("su.plo:pv-addon-lavaplayer-lib:${project.property("pv_addon_lavaplayer_lib_version")}")
    implementation("org.commonmark:commonmark:${project.property("commonmark_version")}")

    // HTTP-сервер ресурсов
    include(implementation("com.squareup.okhttp3:okhttp:${project.property("okhttp_version")}")!!)
    include(implementation("com.squareup.okio:okio-jvm:${project.property("okio_version")}")!!)
    include(implementation("com.github.codeborne.klite:klite-server:${project.property("klite_version")}")!!)
    include(implementation("com.github.codeborne.klite:klite-core:${project.property("klite_version")}")!!)
    include(implementation("su.plo.slib:api-server:${project.property("slib_api_server_version")}")!!)

    include(implementation(project.dependencies.platform("io.insert-koin:koin-bom:${project.property("koin_version")}"))!!)
    include(implementation("io.insert-koin:koin-core")!!)

    include(implementation("org.mongodb:mongodb-driver-sync:5.2.1")!!)
    include(implementation("org.mongodb:mongodb-driver-core:5.2.1")!!)
    include(implementation("org.mongodb:bson:5.2.1")!!)
    include(implementation("org.mongodb:bson-record-codec:5.2.1")!!)
    include(implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")!!)
    include(implementation("com.fasterxml.jackson.core:jackson-core:2.17.1")!!)
    include(implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.1")!!)
    include(implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")!!)
    include(implementation("org.litote.kmongo:kmongo-core:5.1.0")!!)
    include(implementation("org.litote.jackson:jackson-module-loader:0.4.0")!!)
    include(implementation("org.litote.kmongo:kmongo-data:5.1.0")!!)
    include(implementation("org.litote.kmongo:kmongo-id:5.1.0")!!)
    include(implementation("org.litote.kmongo:kmongo-id-jackson:5.1.0")!!)
    include(implementation("org.litote.kmongo:kmongo-jackson-mapping:5.1.0")!!)
    include(implementation("org.litote.kmongo:kmongo-property:5.1.0")!!)
    include(implementation("org.litote.kmongo:kmongo-shared:5.1.0")!!)
    include(implementation("de.undercouch:bson4jackson:2.15.1")!!)
    include(implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")!!)
    include(implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")!!)
    include(implementation("net.kyori:adventure-text-minimessage:4.14.0")!!)
    include(implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.0")!!)
    include(implementation("com.fasterxml.jackson.datatype:jackson-datatype-guava:2.17.+")!!)
    compileOnly("net.luckperms:api:5.4")

    //Конфигурация
    modImplementation(("maven.modrinth:midnightlib:${project.property("midnightlib_version")}")  )

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
}

tasks.runClient { args =
    (args?.plus(arrayOf("--gameDir", "client", "--add-opens", "java.base/java.util=ALL-UNNAMED")) ?: arrayOf("--gameDir", "client", "--add-opens", "java.base/java.util=ALL-UNNAMED")) as List<String?>
}

tasks.shadowJar {
    relocate("kotlin", "su.plo.voice.libs.kotlin")
    relocate("kotlinx.coroutines", "su.plo.voice.libs.kotlinx.coroutines")
    relocate("kotlinx.serialization", "su.plo.voice.libs.kotlinx.serialization")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version"),
            "kotlin_loader_version" to project.property("kotlin_loader_version")
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
    options.compilerArgs.add("-parameters")
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}