import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    kotlin("jvm") version "1.4.32"
}

group = "dev.minn"
version = "0.8.0-alpha.1"

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly("net.dv8tion:JDA:5.0.0-alpha.1")
    api(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val javadoc: Javadoc by tasks

val sourcesJar = task<Jar>("sourcesJar") {
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

val javadocJar = task<Jar>("javadocJar") {
    from(javadoc.destinationDir)
    archiveClassifier.set("javadoc")

    dependsOn(javadoc)
}

tasks {
    build {
        dependsOn(javadocJar)
        dependsOn(sourcesJar)
        dependsOn(jar)
    }
}

publishing.publications {
    register<MavenPublication>("Release") {
        from(components["java"])
        groupId = project.group as String
        artifactId = project.name
        version = project.version as String

        artifact(javadocJar)
        artifact(sourcesJar)
    }
}
