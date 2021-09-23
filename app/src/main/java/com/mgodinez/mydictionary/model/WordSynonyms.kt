/*
 * Filename: WordSynonyms.kt
 * Description: Object which holds the Json parsed synonyms associated with the its respective
 *              word.
 *
 */

package com.mgodinez.mydictionary.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WordSynonyms(
    @field:Json(name = "word") val word: String,
    @field:Json(name = "synonyms") val synonyms: List<String>,
) // End of class WordSynonyms