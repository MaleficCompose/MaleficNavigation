package xyz.malefic.navigate.config

import androidx.compose.runtime.Composable
import java.io.InputStream
import org.yaml.snakeyaml.Yaml
import xyz.malefic.navigate.ConfigLoader
import xyz.malefic.navigate.Route

/** A loader that reads route configurations from a YAML file. */
class YamlConfigLoader : ConfigLoader {
  /**
   * Loads routes from the provided YAML input stream.
   *
   * @param composableMap A map of route names to composable functions.
   * @param inputStream The input stream of the YAML file.
   * @return A list of routes.
   */
  override fun loadRoutes(
    composableMap: Map<String, @Composable (List<String?>) -> Unit>,
    inputStream: InputStream,
  ): List<Route> {
    val yaml = Yaml()
    val data: Map<String, Any> = yaml.load(inputStream)
    return processRoutes(data, composableMap)
  }
}
