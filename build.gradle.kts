/*
 * Copyright (c) 2022.
 * Islander by Koding Dev <hello@koding.dev>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("dev.architectury.loom") version "0.11.0-SNAPSHOT"
    id("io.github.juuxel.loom-quiltflower") version "1.7.3"

    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
}

apply(plugin = "java")
apply(plugin = "maven-publish")
apply(plugin = "dev.architectury.loom")
apply(plugin = "org.jetbrains.kotlin.jvm")
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

version = rootProject.property("mod_version").toString()
group = rootProject.property("maven_group").toString()

repositories {
    mavenCentral()
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven("https://maven.terraformersmc.com/releases")
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
        content {
            includeGroup("maven.modrinth")
        }
    }

    // For DiscordIPC atm
    maven("https://jitpack.io")
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.property("minecraft_version")}")
    mappings(project.the<LoomGradleExtensionAPI>().officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")

    // Dev mods
    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:1.1.0")
    modRuntimeOnly("com.terraformersmc:modmenu:4.0.6")
    modRuntimeOnly("maven.modrinth:lazydfu:0.1.3")

    // Kotlin
    modImplementation("net.fabricmc:fabric-language-kotlin:1.8.3+kotlin.1.7.10")

    // DiscordIPC
    implementation("com.github.jagrosh:DiscordIPC:-SNAPSHOT")

    // projectlombok
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
    testCompileOnly("org.projectlombok:lombok:1.18.24")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.24")
}

tasks {
    named<JavaCompile>("compileJava") {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    named<ProcessResources>("processResources") {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    remapJar {
        dependsOn("build")
        archiveClassifier.set(null as String?)
    }

    jar {
        archiveClassifier.set("dev")
    }
}

configure<BasePluginExtension> {
    archivesName.set(rootProject.property("archives_base_name").toString())
}

configure<LoomGradleExtensionAPI> {
    silentMojangMappingsLicense()
    runConfigs.remove(runConfigs["server"])
}

configure<JavaPluginExtension> {
    withSourcesJar()
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("mavenFabric") {
            from(components["java"])
            artifactId = "${rootProject.property("archives_base_name")}-${project.name}"
        }
    }
}