package xyz.malefic.extensions

/**
 * Extension function for the List class that returns the element at the specified index,
 * or a default value if the index is out of bounds.
 *
 * @param index The index of the element to retrieve.
 * @param default The default value to return if the index is out of bounds.
 * @return The element at the specified index, or the default value if the index is out of bounds.
 */
operator fun <T> List<T>.get(index: Int, default: T): T = getOrNull(index) ?: default
