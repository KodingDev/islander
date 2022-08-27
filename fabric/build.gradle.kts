@file:Suppress("UnstableApiUsage")

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

architectury {
    platformSetupLoomIde()
    fabric()
}

val common: Configuration by configurations.creating { }

// Don't use shadow from the shadow plugin because we don't want IDEA to index this.
val shadowCommon: Configuration by configurations.creating { }

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    this["developmentFabric"].extendsFrom(common)
}

repositories {
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven("https://maven.terraformersmc.com/releases")
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")
    // Remove the next line if you don't want to depend on the API
    modApi("dev.architectury:architectury-fabric:${rootProject.property("architectury_version")}")

    common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionFabric")) { isTransitive = false }

    // Dev mods
    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:1.1.0")
    modRuntimeOnly("com.terraformersmc:modmenu:4.0.6")
    modRuntimeOnly("maven.modrinth:lazydfu:0.1.3")
}

tasks {
    named<ProcessResources>("processResources") {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    shadowJar {
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

publishing {
    publications {
        create<MavenPublication>("mavenFabric") {
            from(components["java"])
            artifactId = "${rootProject.property("archives_base_name")}-${project.name}"
        }
    }
}