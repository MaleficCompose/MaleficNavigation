import cn.lalaki.pub.BaseCentralPortalPlusExtension.PublishingType

val v = "1.0.0"
val localMavenRepo = uri(layout.buildDirectory.dir("repo").get())

plugins {
  kotlin("jvm")
  id("org.jetbrains.compose")
  id("org.jetbrains.kotlin.plugin.compose")
  alias(libs.plugins.spotless)
  id("maven-publish")
  alias(libs.plugins.central)
  signing
}

group = "io.github.omydagreat"
version = v

repositories {
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  google()
}

dependencies {
  implementation(compose.desktop.common)
  implementation(compose.animation)
  implementation(compose.foundation)
  implementation(libs.precompose)
  implementation(libs.snakeyaml)
  implementation(libs.kermit)
}

spotless {
  kotlin {
    ktfmt().googleStyle()
  }
}

java {
  withJavadocJar()
  withSourcesJar()
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = "xyz.malefic"
      artifactId = "maleficnav"
      version = v

      from(components["java"])

      pom {
        name.set("MaleficNav")
        description.set("A Kotlin Compose Desktop Library for navigation meant to be used with PreCompose")
        url.set("https://github.com/OmyDaGreat/MaleficNav")
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
          connection.set("scm:git:git://github.com/OmyDaGreat/MaleficNav.git")
          developerConnection.set("scm:git:ssh://github.com/OmyDaGreat/MaleficNav.git")
          url.set("https://github.com/OmyDaGreat/MaleficNav")
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

signing {
  useGpgCmd()
  sign(publishing.publications)
}

centralPortalPlus {
  url = localMavenRepo
  username = project.findProperty("centralPortalUsername") as String? ?: ""
  password = project.findProperty("centralPortalPassword") as String? ?: ""
  publishingType = PublishingType.AUTOMATIC // or PublishingType.USER_MANAGED
}

tasks.register("updateReadme") {
  group = "centralportal"
  description = "Update the README.md file with the current version"
  doLast {
    val readmeFile = file("README.md")
    val content = readmeFile.readText()
    val updatedContent = content.replace("{maleficnav_version}", v)
    readmeFile.writeText(updatedContent)
  }
}

tasks.named("build") {
  dependsOn("updateReadme")
}
