import cn.lalaki.pub.BaseCentralPortalPlusExtension.PublishingType

val v = "1.1.2"
val localMavenRepo = uri(layout.buildDirectory.dir("repo").get())

plugins {
  kotlin("jvm")
  id("org.jetbrains.compose")
  id("org.jetbrains.kotlin.plugin.compose")
  id("maven-publish")
  alias(libs.plugins.spotless)
  alias(libs.plugins.central)
  alias(libs.plugins.dokka)
  signing
}

group = "xyz.malefic"
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
  implementation(libs.gson)
  implementation(libs.kermit)
  implementation(libs.malefic.extensions)
}

spotless {
  kotlin {
    ktfmt().googleStyle()
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
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
        description.set("A Compose Desktop library for navigation meant to be used alongside PreCompose")
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
  username = System.getenv("centralPortalUsername") ?: ""
  password = System.getenv("centralPortalPassword") ?: ""
  publishingType = PublishingType.AUTOMATIC
}

tasks.dokkaHtml {
  outputDirectory.set(layout.buildDirectory.get().asFile.resolve("dokka"))
}
