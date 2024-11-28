package xyz.malefic.extensions

import moe.tlaster.precompose.navigation.Navigator

/**
 * Navigates to the specified route using the Navigator.
 *
 * @param route The route to navigate to.
 */
infix fun Navigator.gate(route: String) = this.navigate(route)
