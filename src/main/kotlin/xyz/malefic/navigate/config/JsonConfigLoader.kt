package xyz.malefic.navigate.config

import androidx.compose.runtime.Composable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import xyz.malefic.navigate.Route

/** A loader that reads route configurations from a JSON input stream. */
class JsonConfigLoader : ConfigLoader {
  /**
   * Loads routes from a JSON input stream and maps them to composable functions. Returns a pair
   * with the startup route as a string and the list of routes.
   *
   * @param composableMap A map of route names to composable functions.
   * @param inputStream The input stream containing the JSON configuration.
   * @return A pair containing the startup route and a list of routes.
   */
  override fun loadRoutes(
    composableMap: Map<String, @Composable (List<String?>) -> Unit>,
    inputStream: InputStream,
  ): Pair<String, List<Route>> {
    val gson = Gson()
    val reader = inputStream.reader()
    val type = object : TypeToken<Map<String, Any>>() {}.type
    val data: Map<String, Any> = gson.fromJson(reader, type)
    val routes = processRoutes(data, composableMap)
    val startupRoute = data["startup"] as? String ?: "default"
    return Pair(startupRoute, routes)
  }
}
