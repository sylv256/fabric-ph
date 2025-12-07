plugins {
	id("fabric-loom") version "1.14-SNAPSHOT"
	id("maven-publish")
}

val mod_version: String by project
val maven_group: String by project
val archives_base_name: String by project
val minecraft_version: String by project
val loader_version: String by project
val fabric_api_version: String by project

version = mod_version
group = maven_group

base {
	archivesName = archives_base_name
}

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${minecraft_version}")
	mappings(loom.officialMojangMappings())
	modImplementation("net.fabricmc:fabric-loader:${loader_version}")

    // for testing purposes
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${fabric_api_version}")
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand("version" to mod_version)
	}

	filesMatching("**/*.ase") {
		exclude()
	}
}

tasks.withType<JavaCompile>() {
	options.release = 21
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
	inputs.property("archivesName", archives_base_name)

	from("LICENSE") {
		rename { "${it}_${archives_base_name}"}
	}

	from()
}

// configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = archives_base_name
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
