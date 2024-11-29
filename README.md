# MaleficNav Library

MaleficNav is a Kotlin-based navigation library designed for Compose Desktop applications. It provides a simple and flexible way to manage navigation routes, including dynamic and static routes, with support for YAML-based configuration. It is based off and intended for use alongside [PreCompose](https://github.com/Tlaster/PreCompose).

## Features

- **Dynamic and Static Routes**: Define routes with or without parameters.
- **Multi-file Configuration**: Load routes from a YAML, XML, JSON or custom config file.
- **Composable Navigation**: Use composable functions for route content.
- **Navigator Integration**: Seamless integration with PreCompose navigation.
- **Basic Setup**: Comes with a basic implementation that's akin to plug and play.

## Installation

To use MaleficNav in your project, add the following dependencies to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("moe.tlaster:precompose:1.6.2")
    implementation("xyz.malefic:maleficnav:1.1.1")
}
```

## Usage

### 1. Create Composable Functions

Below are a couple I made using the route setup that follows.

```kotlin
@Composable
fun App1(id: String, name: String?) {
  var text by remember { mutableStateOf("Hello, World!") }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Button(onClick = { text = "Hello, Desktop!" }) { Text(text) }
      Spacer(modifier = Modifier.height(16.dp))
      Text("ID: $id")
      name?.let { Text("Name: $name") } ?: run { Text("Unnamed") }
    }
  }
}

@Composable
fun App2(navi: Navigator) {
  var text by remember { mutableStateOf("Hello, World 2!") }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Button(onClick = { text = "Hello, Desktop 2!" }) { Text(text) }
      Spacer(modifier = Modifier.height(16.dp))
      Button(onClick = { navi.navigate("home/123456") }) { Text("Go to App1") }
      Spacer(modifier = Modifier.height(16.dp))
      Button(onClick = { navi.navigate("hidden/boo!") }) { Text("Go to Hidden Page") }
    }
  }
}
```

### 2. Define Routes in a config format

Currently, only YAML, JSON, and XML are supported. Provided is an example of a `routes.yaml` file to define the routes through the application. Each route should have a name and a composable. The hidden aspect decides if it is shown in the sidebar. If you use your own sidebar implementation, which I would highly recommend, then that does not really matter. The parameters should be defined after that. The names of the parameters should be consistent with whatever they are named in the composable. A `?` after a parameter name indicates that it is optional.

```yaml
routes:
  - name: home2
    composable: App2
  - name: home
    composable: App1
    hidden: true
    params:
      - id
      - name?
  - name: hidden
    composable: Text
    hidden: true
    params:
      - text?
startup: home2
```

### 3. Create Navigation Menu

Define the navigation menu using `RoutedSidebar` and `RoutedNavHost` or create your own implementations of them. Everything I used to make them is available through RouteManager and you can look at those two composables in the same location for inspiration.

```kotlin
@Composable
fun NavigationMenu() {
    Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        RouteManager.RoutedSidebar()
        Divider(color = Color.Black, modifier = Modifier.fillMaxHeight().width(1.dp))
        RouteManager.RoutedNavHost()
    }
}
```

### 4. Define Composable Map

Create a map of composable functions that follows whatever you set up. Within the [MaleficExtensions](https://github.com/MaleficCompose/MaleficExtensions) library, there is an extension function of List that allows for specifying a default parameter. Below is a pretty good example of a composable mapping with a variety of different usages.

```kotlin
val composableMap: Map<String, @Composable (List<String?>) -> Unit> = mapOf(
    "App1" to { params -> App1(id = params[0]!!, name = params[1, null]) },
    "App2" to { _ -> App2(RouteManager.navi) },
    "Text" to { params -> Text(text = params[0, "Nope."]) }
)
```

### 5. Initialize RouteManager

Initialize the `RouteManager` in your `main` function. The `ConfigLoader` should be of the same type as your routes file. You can create your own by implementing the interface. The `NavWindow` is a completely set up Window for PreCompose on Desktop coming from [MaleficComponents](https://github.com/MaleficCompose/MaleficComponents), but you can use a regular Window or other composable as long as PreCompose is still set up properly. Make sure to reference the `routes.yaml` (or whatever other config format you use) file in one way or another, with this being a beginner's example:

```kotlin
fun main() = application {
  NavWindow(onCloseRequest = ::exitApplication) {
    MaterialTheme {
      RouteManager.initialize(composableMap, this::class.java.getResourceAsStream("/routes.yaml")!!, YamlConfigLoader())
      NavigationMenu()
    }
  }
}
```

### Finished Example Project Structure

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

## ComposeDesktopTemplate

If you want to start with a completely set up project, you can find the above example [here](https://github.com/OmyDaGreat/ComposeDesktopTemplate).

## Projects Using This Library

### [baka-notes](https://github.com/OmyDaGreat/baka-notes) - A note-taking app by me, for me

If you want to add your project here, you can submit a pull request or reach out to me.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## Contact

For any questions or feedback, please feel free to contact me.

## License

This project is licensed under the terms of the MIT license.
