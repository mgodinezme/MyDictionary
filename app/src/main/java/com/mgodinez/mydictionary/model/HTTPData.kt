/*
 * Filename: HTTPData.kt
 * Description: Contains the HTTP data needed to make the HTTP requests in this app be
 *              easily modifiable.
 *
 */

package com.mgodinez.mydictionary.model

class HTTPData {
    companion object {
        const val WORDSAPI_PREREQUEST_STRING = "https://wordsapiv1.p.rapidapi.com/words/"
        const val WORDSAPI_DEFINITIONS_STRING = "/definitions"
        const val WORDSAPI_SYNONYMS_STRING = "/synonyms"
        const val WORDSAPI_RANDOM_WORD_STRING = "?random=true"

        const val HOST_TITLE = "x-rapidapi-host"
        const val HOST = "wordsapiv1.p.rapidapi.com"

        const val API_KEY_TITLE = "x-rapidapi-key"
        const val API_KEY = "INPUT YOUR PERSONAL API KEY HERE FOR WORDS API"
    } // End of companion object
} // End of class HTTPData