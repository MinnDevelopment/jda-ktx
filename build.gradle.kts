import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.plugability.ConfigurableBlock
import org.jetbrains.dokka.plugability.DokkaJavaPlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.net.URI

plugins {
    `maven-publish`

    kotlin("jvm") version "2.0.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "club.minnced"
version = "0.12.0"
val jdaVersion = "5.0.0"



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
        freeCompilerArgs.addAll("-Xjvm-default=all")
    }
}



////////////////////////////
// Dependency Declaration //
////////////////////////////

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ch.qos.logback:logback-classic:1.5.6")
    compileOnly("club.minnced:discord-webhooks:0.8.4")

    api(kotlin("stdlib"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    implementation("net.dv8tion:JDA:$jdaVersion")
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

tasks.named<DokkaTask>("dokkaHtml").configure {
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

        //FIXME
//        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
//            footerMessage = "Copyright © 2020 Florian Spieß"
//        }
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
    }
}
