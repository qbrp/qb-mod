import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0"
    id("fabric-loom") version "1.9.1"
    id("maven-publish")
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
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // To change the versions see the gradle.properties file
    implementation(kotlin("reflect"))
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    implementation("com.squareup.okhttp3:okhttp:${project.property("okhttp_version")}")
    include(implementation("com.github.codeborne.klite:klite-server:${project.property("klite_version")}")!!)
    include(implementation("com.github.codeborne.klite:klite-core:${project.property("klite_version")}")!!)

    include(implementation("org.mongodb:mongodb-driver-sync:5.2.1")!!)
    implementation("org.mongodb:mongodb-driver-core:5.2.1")
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

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
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
