package xyz.malefic.extensions

/**
 * Extension function for `List<T?>` that allows getting an element at a specified index with a
 * default value if the element is null or the index is out of bounds.
 *
 * @param T The type of elements in the list, which can be nullable.
 * @param index The index of the element to retrieve.
 * @param default The default value to return if the element at the specified index is null or the
 *   index is out of bounds.
 * @return The element at the specified index if it is not null, otherwise the default value.
 */
inline operator fun <reified T> List<T?>.get(index: Int, default: T): T =
  this.getOrNull(index) ?: default
