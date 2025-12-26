package xyz.malefic.compose.nav

import androidx.compose.runtime.Composable

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
    fun static(
        name: String,
        composable: @Composable () -> Unit,
    ) {
        routes.add(StaticRoute(name, { _ -> composable() }))
    }

    /**
     * Adds a dynamic route, configured with parameters.
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
        routes.add(DynamicRoute(name, composable, params.toList()))
    }

    /**
     * Adds a startup route.
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
        static(name, composable)
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
