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
      groupId = "io.github.omydagreat"
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
  username = "O7bqYLbd"
  password = "qyiyvXpDmUU0mxjPTuR1kjrQN7QJ7bEtfgRUbd/0ZvVC"
  publishingType = PublishingType.USER_MANAGED // or PublishingType.AUTOMATIC
}
