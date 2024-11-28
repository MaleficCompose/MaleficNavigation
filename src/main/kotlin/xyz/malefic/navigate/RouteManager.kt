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
import co.touchlab.kermit.Logger
import java.io.InputStream
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import xyz.malefic.extensions.gate

/** Manages the routes for the application. */
@Suppress("MemberVisibilityCanBePrivate", "unused")
object RouteManager {
  private var isInitialized = false
  val dynamicRoutes: MutableList<DynamicRoute> = mutableListOf()
  val staticRoutes: MutableList<StaticRoute> = mutableListOf()
  val allRoutes: List<Route>
    get() = staticRoutes + dynamicRoutes

  lateinit var navi: Navigator
  lateinit var startupRoute: String

  /**
   * Initializes the RouteManager with the provided routes and navigator.
   *
   * @param composableMap A map of composable functions.
   * @param inputStream The input stream to load routes from.
   * @param configLoader The configuration loader to use.
   * @param navi An optional navigator to use.
   */
  fun initialize(
    composableMap: Map<String, @Composable (List<String?>) -> Unit>,
    inputStream: InputStream,
    configLoader: ConfigLoader,
    navi: Navigator? = null,
  ) {
    if (!isInitialized) {
      val routes = configLoader.loadRoutes(composableMap, inputStream)
      routes.forEach { route ->
        when (route) {
          is DynamicRoute -> dynamicRoutes.add(route)
          is StaticRoute -> staticRoutes.add(route)
        }
      }
      startupRoute = routes.firstOrNull { !it.hidden }?.name ?: "default"
      isInitialized = true
    }
    navi?.let { this.navi = it }
  }

  /**
   * Ensures that the RouteManager is initialized.
   *
   * @throws IllegalStateException if the RouteManager is not initialized.
   */
  private fun ensureInitialized() {
    check(isInitialized) {
      throw IllegalStateException(
        "RouteManager is not initialized. Call RouteManager.initialize() first."
      )
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
      throw IllegalStateException(
        "RouteManager is not initialized. Call RouteManager.initialize() first."
      )
    }
    if (!::navi.isInitialized) {
      navi = rememberNavigator()
    }
  }

  /**
   * Displays the navigation host with the provided startup route.
   *
   * @param startupRoute The initial route to display.
   */
  @Composable
  fun RoutedNavHost(startupRoute: String = this.startupRoute) {
    composableEnsureInitialized()
    NavHost(navi, initialRoute = startupRoute) {
      dynamicRoutes.forEach { route ->
        Logger.d("Adding dynamic route ${route.name}")
        scene(route.fullName) { params -> route.composable(params.pathMap.values.toList()) }
      }
      staticRoutes.forEach { route ->
        Logger.d("Adding static route ${route.name}")
        scene(route.name) { route.composable(emptyList()) }
      }
    }
  }

  /** Displays the sidebar with buttons for each non-hidden route. */
  @Composable
  fun RoutedSidebar() {
    composableEnsureInitialized()
    Column(
      modifier = Modifier.width(200.dp).fillMaxHeight(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      getNonHiddenRoutes().forEach { route ->
        Button(onClick = { navi gate route.name }) { Text(route.name.capitalize(Locale.current)) }
      }
    }
  }

  /**
   * Returns a list of all non-hidden routes.
   *
   * @return A list of non-hidden routes.
   */
  fun getNonHiddenRoutes(): List<Route> {
    ensureInitialized()
    return allRoutes.filter { !it.hidden }
  }
}
