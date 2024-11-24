plugins {
  kotlin("jvm")
  id("org.jetbrains.compose")
  id("org.jetbrains.kotlin.plugin.compose")
  alias(libs.plugins.spotless)
}

group = "xyz.malefic"
version = "1.0.0"

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
