import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:1.+")
    }

    dependencyLocking.lockAllConfigurations()
}

plugins {
    `maven-publish`
    signing

    kotlin("jvm") version "1.+"
    id("io.github.gradle-nexus.publish-plugin") version "1.+"
    id("io.gitlab.arturbosch.detekt") version "1.+"
    id("org.jetbrains.dokka") version "1.+"
}

group = "club.minnced"
version = "0.11.0"
val jdaVersion = "5.0.0"



////////////////////////////////
// Dependency Version Locking //
////////////////////////////////

fun ComponentSelectionRules.notRc() {
    all {
        if (candidate.version.contains("RC", ignoreCase = true))
            reject("not a release version")
    }
}

// To update lockfile, run ./gradlew dependencies --update-locks '<group id>:<artifact id>'
// To update all locks use ./gradlew dependencies --write-locks

configurations {
    compileClasspath {
        resolutionStrategy.activateDependencyLocking()
        resolutionStrategy.componentSelection.notRc()
    }
    runtimeClasspath {
        resolutionStrategy.activateDependencyLocking()
        resolutionStrategy.componentSelection.notRc()
    }
}

dependencyLocking {
    lockMode.set(LockMode.STRICT)
}



///////////////////////////
// Compile Configuration //
///////////////////////////

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf(
        "-Xjvm-default=all",  // use default methods in interfaces
        "-Xlambdas=indy"      // use invokedynamic lambdas instead of synthetic classes
    )
}



////////////////////////////
// Dependency Declaration //
////////////////////////////

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ch.qos.logback:logback-classic:latest.release")
    compileOnly("club.minnced:discord-webhooks:latest.release")

    api(kotlin("stdlib"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.+")

    implementation("net.dv8tion:JDA:$jdaVersion")
}



////////////////////////
// Task Configuration //
////////////////////////

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

tasks.getByName("dokkaHtml", DokkaTask::class) {
    dokkaSourceSets.configureEach {
        includes.from("packages.md")
        jdkVersion.set(8)
        sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl.set(URI("https://github.com/MinnDevelopment/jda-ktx/tree/master/src/main/kotlin").toURL())
            remoteLineSuffix.set("#L")
        }

        externalDocumentationLink(
            URI("https://ci.dv8tion.net/job/JDA5/javadoc/").toURL(),
            URI("https://ci.dv8tion.net/job/JDA5/javadoc/element-list").toURL()
        )

        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
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


publishing.publications {
    register<MavenPublication>("Release") {
        from(components["java"])
        artifactId = project.name
        groupId = project.group as String
        version = project.version as String

        artifact(javadocJar)
        artifact(sourcesJar)

        pom.apply(generatePom())

        // Uses the resolved version instead of the 1.+ wildcards
        // See https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:resolved_dependencies
        versionMapping {
            usage("java-api") {
                fromResolutionOf("runtimeClasspath")
            }
            usage("java-runtime") {
                fromResolutionResult()
            }
        }
    }
}

val signingKey: String? by project
val signingKeyId: String? by project
val ossrhUser: String? by project
val ossrhPassword: String? by project
val stagingProfile: String? by project

val enablePublishing = ossrhUser != null && ossrhPassword != null && stagingProfile != null

signing {
    useInMemoryPgpKeys(signingKeyId, signingKey, "")
    sign(*publishing.publications.toTypedArray())
    isRequired = enablePublishing
}

if (enablePublishing) {
    nexusPublishing {
        repositories.sonatype {
            username.set(ossrhUser)
            password.set(ossrhPassword)
            stagingProfileId.set(stagingProfile)
        }
    }
}

val build by tasks
val clean by tasks

build.mustRunAfter(clean)

val rebuild = tasks.register<Task>("rebuild") {
    group = "build"
    dependsOn(clean, build)
}

// Only enable publishing task for properly configured projects
val publishingTasks = tasks.withType<PublishToMavenRepository> {
    enabled = ossrhUser?.isNotEmpty() == true
    mustRunAfter(rebuild)
    dependsOn(rebuild)
}
