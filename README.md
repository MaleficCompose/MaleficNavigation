# MaleficNavigation: Flexible Navigation for Compose Desktop

## Overview

MaleficNavigation is a powerful, lightweight navigation library designed specifically for Compose Desktop applications. Built to seamlessly integrate with [PreCompose](https://github.com/Tlaster/PreCompose), it provides developers with a flexible and intuitive routing solution.

## 🌟 Key Features

- **Versatile Route Configuration**
  - Support for dynamic and static routes
  - Multiple configuration formats (YAML, JSON, XML, custom)
  - Compile-time route definitions via Kotlin DSL

- **Composable-First Design**
  - Use composable functions directly as route content
  - Flexible parameter handling for routes
  - Easy integration with PreCompose navigation

- **Configuration Flexibility**
  - Multi-file configuration support
  - Custom config loader implementation
  - Hidden route capabilities

## 📦 Installation

Add these dependencies to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("moe.tlaster:precompose:1.6.2")
    implementation("xyz.malefic.compose:nav:1.3.0")
}
```

## 🚀 Quick Start

### 1. Define Composable Routes

```kotlin
@Composable
fun App1(id: String, name: String?) {
    // Your route content
}

@Composable
fun App2(navi: Navigator) {
    // Another route content
}
```

### 2. Configure Routes

#### Traditional Approach (with Composable Map)
```kotlin
// Requires a separate composable map
val composableMap: Map<String, @Composable (List<String?>) -> Unit> = mapOf(
    "App1" to { params -> App1(id = params[0]!!, name = params[1]) },
    "App2" to { _ -> App2(RouteManager.navi) }
)

// Initialization with composable map
RouteManager.initialize(
    composableMap, 
    resourceStream("/routes.yaml"), 
    YamlConfigLoader()
)
```

#### 🆕 Kotlin DSL Approach (Recommended)
```kotlin
// No separate composable map needed!
RouteManager.initialize {
    // Direct route definition with inline composables
    route("home", hidden = true) { params -> 
        App1(id = params[0]!!, name = params[1]) 
    }
    
    // Start-up route with direct composable
    startupRoute("home2") { _ -> 
        App2(RouteManager.navi) 
    }
    
    // Hidden route with optional parameters
    hiddenRoute("hidden", "text?") { params -> 
        Text(text = params[0] ?: "Default Text") 
    }
}
```

**Key Advantages of the Kotlin DSL:**
- No need to create a separate composable map
- Routes defined directly during initialization
- Type-safe parameter handling
- Inline composable definitions
- More readable and concise configuration
- Compile-time checking of routes

### 3. Create Navigation Menu

```kotlin
@Composable
fun NavigationMenu() {
    Row {
        RouteManager.RoutedSidebar()
        RouteManager.RoutedNavHost()
    }
}
```

### 4. Initialize in Main Function

```kotlin
fun main() = application {
    NavWindow(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            // Using Kotlin DSL - no composable map required
            RouteManager.initialize {
                route("home") { _ -> MainScreen() }
                startupRoute("dashboard") { _ -> DashboardScreen() }
            }
            NavigationMenu()
        }
    }
}
```

## 📂 Project Structure Example

```
src/
├── main/
│   ├── kotlin/
│   │   ├── screens/
│   │   │   ├── App1.kt
│   │   │   ├── App2.kt
│   │   ├── Main.kt
│   ├── resources/
│   │   └── routes.yaml
```

## 🛠 Advanced Configuration

### Custom Config Loader

Implement the `ConfigLoader` interface to support your own configuration format:

```kotlin
interface ConfigLoader {
    fun loadRoutes(
        composableMap: Map<String, @Composable (List<String?>) -> Unit>,
        inputStream: InputStream
    ): Pair<String, List<Route>>
}
```

## 🤝 Contributing

Contributions are welcome! Please:
- Open an issue to discuss proposed changes
- Submit pull requests with clear descriptions
- Follow existing code style and conventions

## 📄 License

MIT License

## 🌐 Related Projects

- [PreCompose](https://github.com/Tlaster/PreCompose)
- [ComposeDesktopTemplate](https://github.com/MaleficCompose/ComposeDesktopTemplate)

### Projects Using MaleficNavigation

*Want to add your project? Submit a pull request or reach out!*

## 📞 Contact

For questions, feedback, or support, please open an issue on GitHub.
