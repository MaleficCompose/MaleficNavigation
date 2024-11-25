package xyz.malefic.navigate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import java.io.InputStream
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import org.yaml.snakeyaml.Yaml

/** Object responsible for managing navigation routes. */
@Suppress("MemberVisibilityCanBePrivate")
object RouteManager {
  /** List of dynamic routes that can have parameters. */
  val dynamicRoutes: MutableList<DynamicRoute> = mutableListOf()

  /** List of static routes that do not have parameters. */
  val staticRoutes: MutableList<StaticRoute> = mutableListOf()

  /** Combined list of all routes, both static and dynamic. */
  val allRoutes: List<Route>
    get() = staticRoutes + dynamicRoutes

  /** Flag indicating whether the RouteManager has been initialized. */
  private var isInitialized = false

  /** Navigator instance used for navigation. */
  lateinit var navigator: Navigator

  /** The startup route to be displayed initially. */
  lateinit var startupRoute: String

  /** Input stream for reading the YAML configuration. */
  private lateinit var inputYaml: InputStream

  /**
   * Initializes the RouteManager with the provided composable map and input stream.
   *
   * @param composableMap A map of composable functions keyed by their names.
   * @param inputStream The input stream containing the YAML configuration.
   * @param navi An optional Navigator instance. If not provided, one will be made and can be
   *   accessed via [navigator].
   */
  fun initialize(
    composableMap: Map<String, @Composable (List<String?>) -> Unit>,
    inputStream: InputStream,
    navi: Navigator? = null,
  ) {
    if (!isInitialized) {
      inputYaml = inputStream
      loadRoutesFromYaml(composableMap)
      isInitialized = true
    }
    navi?.let { navigator = navi }
  }

  /**
   * Ensures that the RouteManager is initialized.
   *
   * @throws IllegalStateException if the RouteManager is not initialized.
   */
  private fun ensureInitialized() {
    check(isInitialized) {
      throw IllegalStateException("RouteManager is not initialized. Call initialize() first.")
    }
  }

  /**
   * Ensures that the RouteManager is initialized in a composable context.
   *
   * @throws IllegalStateException if the RouteManager is not initialized.
   */
  @Composable
  private fun composableEnsureInitialized() {
    check(isInitialized) {
      throw IllegalStateException("RouteManager is not initialized. Call initialize() first.")
    }
    if (!::navigator.isInitialized) {
      navigator = rememberNavigator()
    }
  }

  /**
   * Retrieves a composable function by its name from the provided composable map.
   *
   * @param composableName The name of the composable function.
   * @param composableMap The map of composable functions.
   * @return The composable function, or a default function displaying "Unknown route".
   */
  private fun getComposableByName(
    composableName: String,
    composableMap: Map<String, @Composable (List<String?>) -> Unit>,
  ): @Composable (List<String?>) -> Unit =
    composableMap[composableName] ?: { _ -> Text("Unknown route") }

  /**
   * Loads routes from the provided YAML input stream.
   *
   * @param composableMap The map of composable functions.
   */
  private fun loadRoutesFromYaml(composableMap: Map<String, @Composable (List<String?>) -> Unit>) {
    val yaml = Yaml()
    val inputStream: InputStream = inputYaml
    val data: Map<String, Any> = yaml.load(inputStream)

    loadData(data, composableMap)

    startupRoute = data["startup"] as? String ?: getNonHiddenRoutes().first().name
    println("Startup Route: $startupRoute")
  }

  /**
   * Loads route data from the provided map.
   *
   * @param data The map containing route data.
   * @param composableMap The map of composable functions.
   */
  private fun loadData(
    data: Map<String, Any>,
    composableMap: Map<String, @Composable (List<String?>) -> Unit>,
  ) {
    val routes = data["routes"]
    if (routes is List<*>) {
      routes.forEach { route ->
        if (route is Map<*, *>) {
          processRoute(route, composableMap)
        }
      }
    }
  }

  /**
   * Processes a single route from the provided map.
   *
   * @param route The map containing route data.
   * @param composableMap The map of composable functions.
   */
  private fun processRoute(
    route: Map<*, *>,
    composableMap: Map<String, @Composable (List<String?>) -> Unit>,
  ) {
    val name = route["name"] as? String ?: return
    val composableName = route["composable"] as? String ?: return
    val composable = getComposableByName(composableName, composableMap)
    val hidden = route["hidden"] == true
    val params = route["params"]
    if (params is List<*>) {
      processParams(name, composable, hidden, params)
    } else {
      staticRoutes.add(StaticRoute(name, composable, hidden))
    }
    println("Loaded route: $name with params: $params")
  }

  /**
   * Processes route parameters and adds the route to the appropriate list.
   *
   * @param name The name of the route.
   * @param composable The composable function for the route.
   * @param hidden A boolean indicating if the route is hidden.
   * @param params The list of parameters for the route.
   */
  private fun processParams(
    name: String,
    composable: @Composable (List<String?>) -> Unit,
    hidden: Boolean,
    params: List<*>,
  ) {
    val stringParams = params.filterIsInstance<String>()
    if (stringParams.size == params.size) {
      dynamicRoutes.add(DynamicRoute(name, composable, hidden, stringParams))
    } else {
      staticRoutes.add(StaticRoute(name, composable, hidden))
    }
  }

  /**
   * Retrieves a list of non-hidden routes.
   *
   * @return A list of non-hidden routes.
   */
  fun getNonHiddenRoutes(): List<Route> {
    ensureInitialized()
    return allRoutes.filter { !it.hidden }
  }

  /**
   * A basic composable function that sets up a navigation host with the initial route.
   *
   * You might want to make your own instead of relying on this basic setup.
   *
   * @param initialRoute The initial route to display.
   */
  @Composable
  fun RoutedNavHost(initialRoute: String = startupRoute) {
    composableEnsureInitialized()
    NavHost(navigator, initialRoute = initialRoute) {
      dynamicRoutes.forEach { route ->
        println("Adding dynamic route ${route.name}")
        scene(route.fullName) { params -> route.composable(params.pathMap.values.toList()) }
      }
      staticRoutes.forEach { route ->
        println("Adding static route ${route.name}")
        scene(route.name) { route.composable(emptyList()) }
      }
    }
  }

  /**
   * A basic composable function that sets up a sidebar with buttons for each non-hidden route.
   *
   * You might want to make your own instead of relying on this basic setup.
   */
  @Composable
  fun RoutedSidebar() {
    composableEnsureInitialized()
    Column(
      modifier = Modifier.width(200.dp).fillMaxHeight(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      getNonHiddenRoutes().forEach { route ->
        Button(onClick = { navigator.navigate(route.name) }) {
          Text(route.name.capitalize(Locale.current))
        }
      }
    }
  }
}
