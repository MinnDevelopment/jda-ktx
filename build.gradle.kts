import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import nl.littlerobots.vcu.plugin.resolver.VersionSelectors
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jreleaser.gradle.plugin.tasks.AbstractJReleaserTask
import org.jreleaser.model.Active

plugins {
    `maven-publish`

    kotlin("jvm") version(libs.versions.kotlin)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.versions)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.jreleaser)
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:2.0.0")
    }
}

group = "club.minnced"
version = "0.14.0"



///////////////////////////
// Compile Configuration //
///////////////////////////

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinJvmCompile> {
    compilerOptions {
        allWarningsAsErrors.set(true)
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}



////////////////////////////
// Dependency Declaration //
////////////////////////////

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.logback)
    compileOnly(libs.webhooks)

    api(libs.kotlin)
    api(libs.coroutines)

    implementation(libs.jda)
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }

    gradleReleaseChannel = "current"
}

versionCatalogUpdate {
    versionSelector(VersionSelectors.STABLE)
}



////////////////////////
// Task Configuration //
////////////////////////

val javadoc: Javadoc by tasks

val sourcesJar by tasks.registering(Jar::class) {
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

val javadocJar by tasks.registering(Jar::class) {
    from(javadoc.destinationDir)
    archiveClassifier.set("javadoc")

    dependsOn(javadoc)
}

tasks.build {
    dependsOn(javadocJar)
    dependsOn(sourcesJar)
    dependsOn(tasks.jar)
}



/////////////
// Linting //
/////////////


detekt {
    parallel = true
    autoCorrect = false

    config.from(files("detekt.yml"))
}

tasks.test.get().dependsOn(tasks.getByName("detekt"))



///////////////////
// Documentation //
///////////////////

dokka {
    dokkaSourceSets.main {
        includes.from("packages.md")
        jdkVersion.set(8)

        sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl("https://github.com/MinnDevelopment/jda-ktx/tree/master/src/main/kotlin")
            remoteLineSuffix.set("#L")
        }

        externalDocumentationLinks.register("JDA") {
            url("https://docs.jda.wiki/")
            packageListUrl("https://docs.jda.wiki/element-list")
        }

        pluginsConfiguration.html {
            footerMessage = "Copyright © 2020 Florian Spieß"
        }
    }
}



////////////////
// Publishing //
////////////////

// Generate pom file for maven central

fun generatePom(): MavenPom.() -> Unit {
    return {
        packaging = "jar"
        name.set(project.name)
        description.set("Collection of useful Kotlin extensions for JDA")
        url.set("https://github.com/MinnDevelopment/jda-ktx")
        scm {
            url.set("https://github.com/MinnDevelopment/jda-ktx")
            connection.set("scm:git:git://github.com/MinnDevelopment/jda-ktx")
            developerConnection.set("scm:git:ssh:git@github.com:MinnDevelopment/jda-ktx")
        }
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("Minn")
                name.set("Florian Spieß")
                email.set("business@minn.dev")
            }
        }
    }
}

val stagingDirectory = layout.buildDirectory.dir("staging-deploy").get()

publishing {
    publications {
        register<MavenPublication>("Release") {
            from(components["java"])
            artifactId = project.name
            groupId = project.group as String
            version = project.version as String

            artifact(javadocJar)
            artifact(sourcesJar)

            pom.apply(generatePom())
        }

        repositories.maven {
            url = stagingDirectory.asFile.toURI()
        }
    }
}

jreleaser {
    project {
        versionPattern = "CUSTOM"
    }

    release {
        github {
            enabled = false
        }
    }

    signing.pgp {
        active = Active.RELEASE
        armored = true
    }

    deploy {
        maven {
            mavenCentral {
                register("sonatype") {
                    active = Active.RELEASE
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository(stagingDirectory.asFile.relativeTo(projectDir).path)
                }
            }
        }
    }
}

tasks.withType<AbstractJReleaserTask>().configureEach {
    mustRunAfter(tasks.named("publish"))
}
