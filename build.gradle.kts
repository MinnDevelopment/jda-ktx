import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:1.6.21")
    }
}

plugins {
    `maven-publish`
    kotlin("jvm") version "1.6.10"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("org.jetbrains.dokka") version "1.6.21"
}

group = "dev.minn"
version = "0.8.7-alpha.11"

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

//kotlin {
//    sourceSets.all {
//        languageSettings.apply {
//            languageVersion = "1.7"
//        }
//    }
//}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf(
        "-Xjvm-default=all",  // use default methods in interfaces
        "-Xlambdas=indy"      // use invokedynamic lambdas instead of synthetic classes
    )
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly("net.dv8tion:JDA:5.0.0-alpha.11")
    compileOnly("ch.qos.logback:logback-classic:1.2.10")
    compileOnly("club.minnced:discord-webhooks:0.7.5")
    api(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
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


detekt {
    parallel = true
    autoCorrect = false

    config = files("detekt.yml")
}

tasks.test.get().dependsOn(tasks.getByName("detekt"))


tasks.getByName("dokkaHtml", DokkaTask::class) {
    dokkaSourceSets.configureEach {
        includes.from("packages.md")
        jdkVersion.set(8)
        sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl.set(URL("https://github.com/MinnDevelopment/jda-ktx/tree/master/src/main/kotlin"))
            remoteLineSuffix.set("#L")
        }

        externalDocumentationLink(
            URL("https://ci.dv8tion.net/job/JDA5/javadoc/"),
            URL("https://ci.dv8tion.net/job/JDA5/javadoc/element-list")
        )

        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
            footerMessage = "Copyright © 2020 Florian Spieß"
        }
    }

}
