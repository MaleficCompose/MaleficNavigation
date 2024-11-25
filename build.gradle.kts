val v = "1.0.0"

plugins {
  kotlin("jvm")
  id("org.jetbrains.compose")
  id("org.jetbrains.kotlin.plugin.compose")
  alias(libs.plugins.spotless)
  id("maven-publish")
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
  implementation(libs.kermit)
}

spotless {
  kotlin {
    ktfmt().googleStyle()
  }
}

publishing {
  publications {
    create<MavenPublication>("github") {
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
            id.set("Malefic")
            name.set("Om Gupta")
            email.set("om@malefic.xyz")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/OmyDaGreat/MaleficNav.git")
          developerConnection.set("scm:git:ssh://github.com/OmyDaGreat/MaleficNav.git")
          url.set("https://github.com/OmyDaGreat/MaleficNav")
        }
      }
    }
  }
  repositories {
    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/OmyDaGreat/MaleficNav")
      credentials {
        username = System.getenv("MALEFIC_USER") ?: "OmyDaGreat"
        password = System.getenv("MALEFIC_PAT_CLASSIC")
      }
    }
  }
}
