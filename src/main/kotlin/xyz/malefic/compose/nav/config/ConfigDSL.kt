package xyz.malefic.compose.nav.config

import androidx.compose.runtime.Composable
import xyz.malefic.compose.nav.DynamicRoute
import xyz.malefic.compose.nav.Route
import xyz.malefic.compose.nav.StaticRoute

/**
 * A builder class for configuring routes in a Compose navigation system.
 */
class ConfigDSL {
    private val routes = mutableListOf<Route>()
    private var startupRoute: String? = null

    /**
     * Adds a static route without parameters.
     *
     * @param name The name of the route.
     * @param composable The composable function to be displayed for this route.
     */
    fun route(
        name: String,
        hidden: Boolean = false,
        composable: @Composable () -> Unit,
    ) {
        routes.add(StaticRoute(name, { _ -> composable() }, hidden))
    }

    /**
     * Adds a dynamic route, configured with parameters.
     *
     * @param name The name of the route.
     * @param hidden Whether the route is hidden.
     * @param params The parameters for the route.
     * @param composable The composable function to be displayed for this route.
     */
    fun route(
        name: String,
        hidden: Boolean = false,
        vararg params: String,
        composable: @Composable (List<String?>) -> Unit,
    ) {
        routes.add(DynamicRoute(name, composable, hidden, params.toList()))
    }

    /**
     * Adds a startup route (marked with *).
     *
     * @param name The name of the route.
     * @param composable The composable function to be displayed for this route.
     */
    fun startupRoute(
        name: String,
        composable: @Composable () -> Unit,
    ) {
        route(name, composable = composable)
        startupRoute = name
    }

    /**
     * Builds the route configuration and returns a pair containing the startup route and the list of routes.
     *
     * @return A pair containing the startup route and the list of routes.
     */
    internal fun build(): Pair<String, List<Route>> =
        Pair(
            startupRoute ?: routes.firstOrNull()?.name ?: "default",
            routes,
        )
}
