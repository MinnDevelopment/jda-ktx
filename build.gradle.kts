import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    kotlin("jvm") version "1.6.10"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
}

group = "dev.minn"
version = "0.8.4-alpha.5"

configure<JavaPluginConvention> {
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

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly("net.dv8tion:JDA:5.0.0-alpha.5")
    compileOnly("ch.qos.logback:logback-classic:1.2.10")
//    compileOnly("club.minnced:discord-webhooks:0.7.5")
    api(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
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
