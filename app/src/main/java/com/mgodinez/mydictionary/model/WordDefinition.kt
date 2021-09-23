/*
 * Filename: WordDefinition.kt
 * Description: Object which holds the definitions(s) associated with the parsed word.
 *
 */

package com.mgodinez.mydictionary.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WordDefinition(
     @field:Json(name = "word") val word: String,
     @field:Json(name = "definitions") val definitions: List<Definitions>,
) // End of class WordDefinition