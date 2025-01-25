import cn.lalaki.pub.BaseCentralPortalPlusExtension.PublishingType

// Basic Information
val user: String by project
val repo: String by project
val g: String by project
val artifact: String by project
val v: String by project
val desc: String by project

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
    implementation(libs.malefic.ext.compose)
    implementation(compose.desktop.common)
    implementation(compose.animation)
    implementation(compose.foundation)
    implementation(libs.precompose)
    implementation(libs.snakeyaml)
    implementation(libs.kermit)
    implementation(libs.gson)
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
    tokenXml = uri(layout.projectDirectory.file("user_token.xml"))
    publishingType = PublishingType.AUTOMATIC
}

// Tasks
tasks.apply {
    register("formatAndLintKotlin") {
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
