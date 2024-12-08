package xyz.malefic.navigate.config

import androidx.compose.runtime.Composable
import java.io.InputStream
import xyz.malefic.navigate.DynamicRoute
import xyz.malefic.navigate.Route
import xyz.malefic.navigate.StaticRoute

/** A loader that reads route configurations from a custom format input stream. */
class MalefiConfigLoader : ConfigLoader {
  /**
   * Loads routes from a MalefiConfig input stream and maps them to composable functions. Returns a
   * pair with the startup route as a string and the list of routes.
   *
   * @param composableMap A map of route names to composable functions.
   * @param inputStream The MalefiConfig input stream containing the configuration.
   * @return A pair containing the startup route and a list of routes.
   */
  override fun loadRoutes(
    composableMap: Map<String, @Composable (List<String?>) -> Unit>,
    inputStream: InputStream,
  ): Pair<String, List<Route>> {
    val reader = inputStream.bufferedReader()
    val lines = reader.readLines()
    val routes = mutableListOf<Route>()
    var startupRoute = "default"

    for (line in lines) {
      val trimmedLine = line.trim()
      if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) continue

      val parts = trimmedLine.split("->").map { it.trim() }
      if (parts.size != 2) continue

      val routePart = parts[0]
      val composablePart = parts[1]

      val name = routePart.removeSuffix("*").trim()
      if (routePart.endsWith("*")) {
        startupRoute = name
      }

      val composableName = composablePart.split("?").first().trim()
      val hidden = composablePart.contains("?")
      val params =
        composablePart
          .substringAfter("[", "")
          .substringBefore("]", "")
          .split(",")
          .map { it.trim() }
          .filter { it.isNotEmpty() }

      val composable =
        composableMap[composableName] ?: { _ -> androidx.compose.material.Text("Unknown route") }

      if (params.isNotEmpty()) {
        routes.add(DynamicRoute(name, composable, hidden, params))
      } else {
        routes.add(StaticRoute(name, composable, hidden))
      }
    }
    return Pair(startupRoute, routes)
  }
}
