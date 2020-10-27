import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    `java-library`
    kotlin("jvm") version "1.3.71"
    id("com.jfrog.bintray") version "1.8.4"
}

group = "dev.minn"
version = "0.2.0"

repositories {
    jcenter()
}

dependencies {
    compileOnly("net.dv8tion:JDA:4.2.0_211")

    api(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val javadoc: Javadoc by tasks

val sourcesJar = task<Jar>("sourcesJar") {
    from(sourceSets["main"].allSource)
    classifier = "sources"
}

val javadocJar = task<Jar>("javadocJar") {
    from(javadoc.destinationDir)
    classifier = "javadoc"

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
    register("BintrayRelease", MavenPublication::class) {
        from(components["java"])
        groupId = project.group as String
        artifactId = project.name
        version = project.version as String

        artifact(javadocJar)
        artifact(sourcesJar)
    }
}

bintray {
    setPublications("BintrayRelease")
    user = properties["bintrayName"] as? String ?: ""
    key  = properties["bintrayKey"] as? String ?: ""
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        setLicenses("Apache-2.0")
        repo = "maven"
        vcsUrl = "https://github.com/MinnDevelopment/jda-ktx"
        githubRepo = "minndevelopment/jda-ktx"
        issueTrackerUrl = "$vcsUrl/issues"
        websiteUrl = vcsUrl
        desc = "Collection of useful Kotlin extensions for JDA. Great in combination with kotlinx-coroutines and jda-reactor."
        setLabels("reactive", "jda", "discord", "kotlin")
        name = project.name
        publish = true
        publicDownloadNumbers = true
        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = project.version as String
            gpg.sign = true
        })
    })
}