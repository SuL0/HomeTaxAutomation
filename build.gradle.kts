import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "kr.sul"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("io.github.bonigarcia", "webdrivermanager", "5.0.3")
    implementation("org.seleniumhq.selenium", "selenium-java", "4.1.0")
    implementation("org.apache.poi", "poi", "5.2.3")
    implementation("org.apache.poi", "poi-ooxml", "5.2.3")
}

tasks {
    jar {
        manifest {
            attributes(Pair("Main-Class", "kr.sul.hometaxautomation.Main"))
        }
    }
    shadowJar {
    }
    build { dependsOn(shadowJar) }
}
