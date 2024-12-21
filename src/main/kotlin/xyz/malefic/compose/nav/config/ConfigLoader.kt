package xyz.malefic.compose.nav.config

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import xyz.malefic.compose.nav.DynamicRoute
import xyz.malefic.compose.nav.Route
import xyz.malefic.compose.nav.StaticRoute
import java.io.InputStream
import kotlin.collections.get

/** Interface for loading and processing routes from a configuration. */
interface ConfigLoader {
    /**
     * Loads routes from an input stream and maps them to composable functions. Returns a pair with
     * the startup route as a string and the list of routes.
     *
     * @param composableMap A map of route names to composable functions.
     * @param inputStream The input stream containing the configuration.
     * @return A pair containing the startup route and a list of routes.
     */
    fun loadRoutes(
        composableMap: Map<String, @Composable (List<String?>) -> Unit>,
        inputStream: InputStream,
    ): Pair<String, List<Route>>

    /**
     * Processes route data and maps them to composables.
     *
     * @param data A map containing route data.
     * @param composableMap A map of route names to composable functions.
     * @return A list of routes.
     */
    fun processRoutes(
        data: Map<String, Any>,
        composableMap: Map<String, @Composable (List<String?>) -> Unit>,
    ): List<Route> {
        val routes = mutableListOf<Route>()
        val routeData = data["routes"] as? List<*> ?: return routes
        routeData.forEach { route ->
            if (route is Map<*, *>) {
                val name = route["name"] as? String ?: return@forEach
                val composableName = route["composable"] as? String ?: return@forEach
                val composable = composableMap[composableName] ?: { _ -> Text("Unknown route") }
                val hidden = route["hidden"] == true
                val params = route["params"] as? List<*>
                if (params != null && params.all { it is String }) {
                    routes.add(DynamicRoute(name, composable, hidden, params.filterIsInstance<String>()))
                } else {
                    routes.add(StaticRoute(name, composable, hidden))
                }
            }
        }
        return routes
    }
}
