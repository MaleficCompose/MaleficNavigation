import androidx.compose.runtime.Composable
import java.io.ByteArrayInputStream
import xyz.malefic.navigate.DynamicRoute
import xyz.malefic.navigate.StaticRoute
import xyz.malefic.navigate.config.MalefiConfigLoader
import kotlin.test.Test
import kotlin.test.assertEquals

class MalefiConfigLoaderTest {

  private val composableMap =
    mapOf<String, @Composable (List<String?>) -> Unit>(
      "Home" to @Composable { _ -> androidx.compose.material.Text("Home") },
      "App1" to @Composable { _ -> androidx.compose.material.Text("App1") },
      "RepoList" to @Composable { _ -> androidx.compose.material.Text("RepoList") },
      "Text" to @Composable { _ -> androidx.compose.material.Text("Text") },
    )

  @Test
  fun `test loadRoutes with valid config`() {
    val config =
      """
            routes:
              home -> Home
              app1 -> App1? [id, name?]
              RepoList* -> RepoList
              hidden -> Text? [text?]
        """
        .trimIndent()

    val inputStream = ByteArrayInputStream(config.toByteArray())
    val loader = MalefiConfigLoader()
    val (startupRoute, routes) = loader.loadRoutes(composableMap, inputStream)

    println(routes)

    assertEquals("RepoList", startupRoute)
    assertEquals(4, routes.size)

    val homeRoute = routes[0] as StaticRoute
    assertEquals("home", homeRoute.name)
    assertEquals(false, homeRoute.hidden)

    val app1Route = routes[1] as DynamicRoute
    assertEquals("app1", app1Route.name)
    assertEquals(true, app1Route.hidden)
    assertEquals(listOf("id", "name?"), app1Route.params)

    val repoListRoute = routes[2] as StaticRoute
    assertEquals("RepoList", repoListRoute.name)
    assertEquals(false, repoListRoute.hidden)

    val hiddenRoute = routes[3] as DynamicRoute
    assertEquals("hidden", hiddenRoute.name)
    assertEquals(true, hiddenRoute.hidden)
    assertEquals(listOf("text?"), hiddenRoute.params)
  }
}
