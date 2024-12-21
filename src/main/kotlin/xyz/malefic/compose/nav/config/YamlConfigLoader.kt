package xyz.malefic.compose.nav.config

import androidx.compose.runtime.Composable
import org.yaml.snakeyaml.Yaml
import xyz.malefic.compose.nav.Route
import java.io.InputStream

/** A loader that reads route configurations from a YAML file. */
class YamlConfigLoader : ConfigLoader {
    /**
     * Loads routes from the provided YAML input stream. Returns a pair with the startup route as a
     * string and the list of routes.
     *
     * @param composableMap A map of route names to composable functions.
     * @param inputStream The input stream of the YAML file.
     * @return A pair containing the startup route and a list of routes.
     */
    override fun loadRoutes(
        composableMap: Map<String, @Composable (List<String?>) -> Unit>,
        inputStream: InputStream,
    ): Pair<String, List<Route>> {
        val yaml = Yaml()
        val data: Map<String, Any> = yaml.load(inputStream)
        val routes = processRoutes(data, composableMap)
        val startupRoute = data["startup"] as? String ?: "default"
        return Pair(startupRoute, routes)
    }
}
