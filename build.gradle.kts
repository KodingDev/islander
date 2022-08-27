import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "0.11.0-SNAPSHOT" apply false

    kotlin("jvm") version "1.7.0" apply false
    kotlin("plugin.serialization") version "1.7.0" apply false
}

architectury {
    minecraft = rootProject.property("minecraft_version").toString()
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    configure<LoomGradleExtensionAPI> {
        silentMojangMappingsLicense()
        runConfigs.remove(runConfigs["server"])
    }

    repositories {
        maven("https://api.modrinth.com/maven") {
            name = "Modrinth"
            content {
                includeGroup("maven.modrinth")
            }
        }
    }

    dependencies {
        "minecraft"("com.mojang:minecraft:${rootProject.property("minecraft_version")}")
        "mappings"(project.the<LoomGradleExtensionAPI>().officialMojangMappings())
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    configure<BasePluginExtension> {
        archivesName.set(rootProject.property("archives_base_name").toString())
    }

    version = rootProject.property("mod_version").toString()
    group = rootProject.property("maven_group").toString()

    tasks {
        named<JavaCompile>("compileJava") {
            options.encoding = "UTF-8"
            options.release.set(17)
        }
    }

    configure<JavaPluginExtension> {
        withSourcesJar()
    }
}
