plugins {
    kotlin("jvm") version "1.3.71"
}

group = "dev.minn"
version = "0.1.0-rc"

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("net.dv8tion:JDA:4.1.1_140")
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