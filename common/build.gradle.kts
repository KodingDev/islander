dependencies {
    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    // Remove the next line if you don't want to depend on the API
    modApi("dev.architectury:architectury:${rootProject.property("architectury_version")}")
}

architectury {
    common("forge", "fabric")
}

publishing {
    publications {
        create<MavenPublication>("mavenCommon") {
            from(components["java"])
            artifactId = rootProject.property("archives_base_name").toString()
        }
    }
}