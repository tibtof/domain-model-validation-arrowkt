repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

dependencies {
    implementation(platform("io.kotest:kotest-bom:5.5.4"))
    implementation("io.arrow-kt:arrow-core:1.1.3")

    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.2.5")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        this.freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

tasks.withType<Test>() {
    useJUnitPlatform()
}

application {
    mainClass.set("cli.AppKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
