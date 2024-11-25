package xyz.malefic.navigate

import androidx.compose.runtime.Composable

/** Interface representing a navigation route. */
interface Route {
  /** The name of the route. */
  val name: String

  /**
   * A composable function that defines the UI for the route.
   *
   * @param params A list of optional parameters for the route.
   */
  val composable: @Composable (List<String?>) -> Unit

  /** A boolean indicating if the route is hidden. */
  val hidden: Boolean
}

/**
 * Data class representing a dynamic route with parameters.
 *
 * @property name The name of the route.
 * @property composable A composable function that defines the UI for the route.
 * @property hidden A boolean indicating if the route is hidden.
 * @property params A list of parameters for the route.
 */
data class DynamicRoute(
  override val name: String,
  override val composable: @Composable (List<String?>) -> Unit,
  override val hidden: Boolean,
  val params: List<String> = emptyList(),
) : Route {
  /** The full name of the route including parameters. */
  val fullName: String
    get() {
      val postfixed = params.map { if (it.endsWith("?")) "${it.dropLast(1)}}?" else "$it}" }
      return "$name/${postfixed.joinToString("/{", prefix = "{")}"
    }
}

/**
 * Data class representing a static route without parameters.
 *
 * @property name The name of the route.
 * @property composable A composable function that defines the UI for the route.
 * @property hidden A boolean indicating if the route is hidden.
 */
data class StaticRoute(
  override val name: String,
  override val composable: @Composable (List<String?>) -> Unit,
  override val hidden: Boolean,
) : Route
