import cn.lalaki.pub.BaseCentralPortalPlusExtension.PublishingType

// Basic Information
val user = "MaleficCompose"
val repo = "MaleficNav"
val g = "xyz.malefic.compose"
val artifact = "nav"
val v = "1.2.0"
val desc = "A Compose Desktop library for navigation meant to be used alongside PreCompose"

val localMavenRepo = uri(layout.buildDirectory.dir("repo").get())

// Plugins
plugins {
    alias(libs.plugins.compose.kotlin)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.compose)
    alias(libs.plugins.central)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

// Dependencies
dependencies {
    implementation(compose.desktop.common)
    implementation(compose.animation)
    implementation(compose.foundation)
    implementation(libs.precompose)
    implementation(libs.snakeyaml)
    implementation(libs.gson)
    implementation(libs.kermit)
    implementation(libs.malefic.extensions)
    testImplementation(kotlin("test"))
}

// Project Information
group = g
version = v

// Repositories
repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

// Java Configuration
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

// Kotlin Configuration
kotlin {
    jvmToolchain {
        this.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// Publishing
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = g
            artifactId = artifact
            version = v

            from(components["java"])

            pom {
                name.set(repo)
                description.set(desc)
                url.set("https://github.com/$user/$repo")
                developers {
                    developer {
                        name.set("Om Gupta")
                        email.set("ogupta4242@gmail.com")
                    }
                }
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/$user/$repo.git")
                    developerConnection.set("scm:git:ssh://github.com/$user/$repo.git")
                    url.set("https://github.com/$user/$repo")
                }
            }
        }
        repositories {
            maven {
                url = localMavenRepo
            }
        }
    }
}

// Signing
signing {
    useGpgCmd()
    sign(publishing.publications)
}

// Central Portal Configuration
centralPortalPlus {
    url = localMavenRepo
    username = System.getenv("centralPortalUsername") ?: ""
    password = System.getenv("centralPortalPassword") ?: ""
    publishingType = PublishingType.AUTOMATIC
}

// Tasks
tasks.apply {
    create("formatAndLintKotlin") {
        group = "formatting"
        description = "Fix Kotlin code style deviations with kotlinter"
        dependsOn(formatKotlin)
        dependsOn(lintKotlin)
    }
    build {
        dependsOn(named("formatAndLintKotlin"))
        dependsOn(dokkaGenerate)
    }
    publish {
        dependsOn(named("formatAndLintKotlin"))
    }
    test {
        useJUnitPlatform()
    }
    check {
        dependsOn("installKotlinterPrePushHook")
    }
}

// Dokka
dokka {
    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
    }
}
