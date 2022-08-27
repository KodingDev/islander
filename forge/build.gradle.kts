@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

architectury {
    platformSetupLoomIde()
    forge()
}

val common: Configuration by configurations.creating { }

// Don't use shadow from the shadow plugin because we don't want IDEA to index this.
val shadowCommon: Configuration by configurations.creating { }

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    this["developmentForge"].extendsFrom(common)
}

repositories {
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

dependencies {
    forge("net.minecraftforge:forge:${rootProject.property("forge_version")}")
    // Remove the next line if you don't want to depend on the API
    modApi("dev.architectury:architectury-forge:${rootProject.property("architectury_version")}")

    common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionForge")) { isTransitive = false }

    // Dev mods
    modRuntimeOnly("me.djtheredstoner:DevAuth-forge-latest:1.1.0")

    // Kotlin
    forgeRuntimeLibrary("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.0")
    forgeRuntimeLibrary("org.jetbrains.kotlin:kotlin-reflect:1.7.0")
    implementation("thedarkcolour:kotlinforforge:3.7.1")
}

tasks {
    named<ProcessResources>("processResources") {
        inputs.property("version", project.version)
        filesMatching("META-INF/mods.toml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        exclude("fabric.mod.json")

        configurations = listOf(shadowCommon)
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
        archiveClassifier.set(null as String?)
    }

    jar {
        archiveClassifier.set("dev")
    }

    // FIXME: Cannot add task 'sourcesJar' as a task with that name already exists
//    sourcesJar {
//        val commonSources = project(":common").task<Jar>("sourcesJar")
//        dependsOn(commonSources)
//        from(commonSources.archiveFile.map { zipTree(it) })
//    }
}

components.named<AdhocComponentWithVariants>("java") {
    withVariantsFromConfiguration(project.configurations.shadowRuntimeElements.get()) {
        skip()
    }
}

configure<LoomGradleExtensionAPI> {
    runConfigs.named("client") {
        property("forge.logging.console.level", "debug")
        property("forge.logging.markers", "SCAN,LOADING,CORE")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenForge") {
            from(components["java"])
            artifactId = "${rootProject.property("archives_base_name")}-${project.name}"
        }
    }
}