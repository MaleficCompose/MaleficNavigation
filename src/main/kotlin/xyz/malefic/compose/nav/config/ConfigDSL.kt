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
     * Adds a static route without parameters or being hidden.
     *
     * @param name The name of the route.
     * @param composable The composable function to be displayed for this route.
     */
    fun static(
        name: String,
        composable: @Composable () -> Unit,
    ) {
        static(name, false, composable)
    }

    /**
     * Adds a static route without parameters.
     *
     * @param name The name of the route.
     * @param hidden Whether the route is hidden, defaulting to false.
     * @param composable The composable function to be displayed for this route.
     */
    fun static(
        name: String,
        hidden: Boolean = false,
        composable: @Composable () -> Unit,
    ) {
        routes.add(StaticRoute(name, { _ -> composable() }, hidden))
    }

    /**
     * Adds a dynamic route, configured with parameters and without being hidden.
     *
     * @param name The name of the route.
     * @param params The parameters for the route.
     * @param composable The composable function to be displayed for this route.
     */
    fun dynamic(
        name: String,
        vararg params: String,
        composable: @Composable (List<String?>) -> Unit,
    ) {
        dynamic(name, false, params = params, composable)
    }

    /**
     * Adds a dynamic route, configured with parameters.
     *
     * @param name The name of the route.
     * @param hidden Whether the route is hidden, defaulting to false.
     * @param params The parameters for the route.
     * @param composable The composable function to be displayed for this route.
     */
    fun dynamic(
        name: String,
        hidden: Boolean = false,
        vararg params: String,
        composable: @Composable (List<String?>) -> Unit,
    ) {
        routes.add(DynamicRoute(name, composable, hidden, params.toList()))
    }

    /**
     * Adds a startup route, without being hidden.
     *
     * The startup route is the route that is displayed when the application is first launched and thus is not allowed to have any parameters.
     *
     * @param name The name of the route.
     * @param composable The composable function to be displayed for this route.
     */
    fun startup(
        name: String,
        composable: @Composable () -> Unit,
    ) {
        startup(name, false, composable)
        startupRoute = name
    }

    /**
     * Adds a startup route.
     *
     * The startup route is the route that is displayed when the application is first launched and thus is not allowed to have any parameters.
     *
     * @param name The name of the route.
     * @param hidden Whether the route is hidden.
     * @param composable The composable function to be displayed for this route.
     */
    fun startup(
        name: String,
        hidden: Boolean = false,
        composable: @Composable () -> Unit,
    ) {
        static(name, hidden, composable = composable)
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
