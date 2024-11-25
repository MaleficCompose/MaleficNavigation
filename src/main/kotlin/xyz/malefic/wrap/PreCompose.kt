package xyz.malefic.wrap

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.ProvidePreComposeLocals

/**
 * A composable window with various customizable properties and everything necessary for a basic
 * precompose desktop app.
 *
 * @param onCloseRequest A lambda function to be called when the window is requested to close.
 * @param state The state of the window, default is a remembered window state.
 * @param visible A boolean indicating if the window is visible, default is true.
 * @param title The title of the window, default is "Untitled".
 * @param icon An optional painter for the window icon, default is null.
 * @param undecorated A boolean indicating if the window is undecorated, default is false.
 * @param transparent A boolean indicating if the window is transparent, default is false.
 * @param resizable A boolean indicating if the window is resizable, default is true.
 * @param enabled A boolean indicating if the window is enabled, default is true.
 * @param focusable A boolean indicating if the window is focusable, default is true.
 * @param alwaysOnTop A boolean indicating if the window is always on top, default is false.
 * @param onPreviewKeyEvent A lambda function to handle preview key events, default returns false.
 * @param onKeyEvent A lambda function to handle key events, default returns false.
 * @param content A composable lambda function to define the content of the window.
 */
@Suppress("kotlin:S107")
@Composable
fun NavWindow(
  onCloseRequest: () -> Unit,
  state: WindowState = rememberWindowState(),
  visible: Boolean = true,
  title: String = "Untitled",
  icon: Painter? = null,
  undecorated: Boolean = false,
  transparent: Boolean = false,
  resizable: Boolean = true,
  enabled: Boolean = true,
  focusable: Boolean = true,
  alwaysOnTop: Boolean = false,
  onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
  onKeyEvent: (KeyEvent) -> Boolean = { false },
  content: @Composable FrameWindowScope.() -> Unit,
) {
  Window(
    onCloseRequest = onCloseRequest,
    state = state,
    visible = visible,
    title = title,
    icon = icon,
    undecorated = undecorated,
    transparent = transparent,
    resizable = resizable,
    enabled = enabled,
    focusable = focusable,
    alwaysOnTop = alwaysOnTop,
    onPreviewKeyEvent = onPreviewKeyEvent,
    onKeyEvent = onKeyEvent,
  ) {
    ProvidePreComposeLocals { PreComposeApp { content() } }
  }
}
