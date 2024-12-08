package xyz.malefic.navigate.config

import androidx.compose.runtime.Composable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import xyz.malefic.navigate.Route

/** A loader that reads route configurations from a JSON input stream. */
class JsonConfigLoader : ConfigLoader {
  /**
   * Loads routes from a JSON input stream and maps them to composable functions.
   *
   * @param composableMap A map of route names to composable functions.
   * @param inputStream The input stream containing the JSON configuration.
   * @return A list of routes.
   */
  override fun loadRoutes(
    composableMap: Map<String, @Composable (List<String?>) -> Unit>,
    inputStream: InputStream,
  ): List<Route> {
    val gson = Gson()
    val reader = inputStream.reader()
    val type = object : TypeToken<Map<String, Any>>() {}.type
    val data: Map<String, Any> = gson.fromJson(reader, type)
    return processRoutes(data, composableMap)
  }

  /**
   * Retrieves the startup route from a JSON input stream.
   *
   * @param inputStream The input stream containing the JSON configuration.
   * @return The startup route as a string, or "default" if not found.
   */
  override fun getStartupRoute(inputStream: InputStream): String {
    val gson = Gson()
    val reader = inputStream.reader()
    val type = object : TypeToken<Map<String, Any>>() {}.type
    val data: Map<String, Any> = gson.fromJson(reader, type)
    return data["startup"] as? String ?: "default"
  }
}
