import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.5.21"

    `java-library`
}

group = "biessek"
version = "0.1-SNAPSHOT"

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation ("systems.danger:danger-kotlin-sdk:1.2")
    implementation ("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.12.3")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testImplementation("io.mockk:mockk:1.12.0")
}

tasks.withType<KotlinCompile>().configureEach {
    val compilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        useIR = true
        kotlinOptions.freeCompilerArgs += compilerArgs
    }
}

tasks.test {
    useJUnitPlatform()
}