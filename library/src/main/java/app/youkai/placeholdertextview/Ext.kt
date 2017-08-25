package app.youkai.placeholdertextview

import android.content.Context

/**
 * Various extension functions
 */

/**
 * Assumes this [Int] is in dp units and converts it into pixels.
 */
internal fun Int.toPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}

/**
 * Assumes this [Int] is in pixel units and converts it into dp units.
 */
internal fun Int.toDp(context: Context): Int {
    return (this / context.resources.displayMetrics.density).toInt()
}