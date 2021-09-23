/*
 * Filename: RandomWord.kt
 * Description: Object which holds the random word for the Word of the Day
 *
 */

package com.mgodinez.mydictionary.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RandomWord(
    @field:Json(name = "word") val word: String,
) // End of class RandomWord