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
    }
  }
  repositories {
    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/OmyDaGreat/MaleficNav")
      credentials {
        username = System.getenv("GITHUB_USERNAME")
        password = System.getenv("GITHUB_TOKEN")
      }
    }
  }
}
