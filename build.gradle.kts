repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.6.20-M1"
}

dependencies {
    implementation("io.arrow-kt:arrow-core:1.0.1")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.0.1")

    testImplementation("io.kotest:kotest-runner-junit5:5.1.0")
    testImplementation("io.kotest:kotest-assertions-core:5.1.0")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.2.3")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        this.freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

tasks.withType<Test>() {
    useJUnitPlatform()
}
