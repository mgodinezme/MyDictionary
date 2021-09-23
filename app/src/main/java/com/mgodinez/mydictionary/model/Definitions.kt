/*
 * Filename: Definitions.kt
 * Description: Object which holds the definition field of a JsonClass for parsing
 *              purposes.
 *
 */

package com.mgodinez.mydictionary.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Definitions(
    val definition: String,
) // End of class Definitions