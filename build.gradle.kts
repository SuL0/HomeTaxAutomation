import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "kr.sul"
version = "1.0"
val javafxVersion = "11.0.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("io.github.bonigarcia", "webdrivermanager", "5.0.3")
    implementation("org.seleniumhq.selenium", "selenium-java", "4.1.0")
    runtimeOnly("org.openjfx", "javafx-controls", javafxVersion)
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

javafx {
    version = javafxVersion
    modules = listOf("javafx.controls")
}
