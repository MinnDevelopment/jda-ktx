plugins {
    `maven-publish`
    kotlin("jvm") version "1.3.71"
}

group = "dev.minn"
version = "0.1.0-rc"

repositories {
    jcenter()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("com.github.dv8fromtheworld:jda:78d0846")
//    compileOnly("net.dv8tion:JDA:4.1.1_141")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

publishing.publications {
    register("MavenPublication", MavenPublication::class) {
        from(components["java"])
        groupId = project.group as String
        artifactId = project.name
        version = project.version as String
    }
}