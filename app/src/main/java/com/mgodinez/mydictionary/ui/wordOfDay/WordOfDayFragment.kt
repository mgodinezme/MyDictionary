/*
 * Filename: WordOfDayFragment.kt
 * Description: Fragment class managing the content of the WordOfDay section of this project.
 *              Fragment is activated when application starts or when its respective bottom tab
 *              is clicked/pressed.
 *
 */

package com.mgodinez.mydictionary.ui.wordOfDay

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mgodinez.mydictionary.databinding.FragmentWordOfDayBinding
import com.mgodinez.mydictionary.model.HTTPData.Companion.API_KEY
import com.mgodinez.mydictionary.model.HTTPData.Companion.API_KEY_TITLE
import com.mgodinez.mydictionary.model.HTTPData.Companion.HOST
import com.mgodinez.mydictionary.model.HTTPData.Companion.HOST_TITLE
import com.mgodinez.mydictionary.model.HTTPData.Companion.WORDSAPI_DEFINITIONS_STRING
import com.mgodinez.mydictionary.model.HTTPData.Companion.WORDSAPI_PREREQUEST_STRING
import com.mgodinez.mydictionary.model.HTTPData.Companion.WORDSAPI_RANDOM_WORD_STRING
import com.mgodinez.mydictionary.model.HTTPData.Companion.WORDSAPI_SYNONYMS_STRING
import com.mgodinez.mydictionary.model.RandomWord
import com.mgodinez.mydictionary.model.WordDefinition
import com.mgodinez.mydictionary.model.WordSynonyms
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.*
import okio.IOException

class WordOfDayFragment : Fragment() {

    private lateinit var wordOfDayViewModel: WordOfDayViewModel
    private var _binding: FragmentWordOfDayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        wordOfDayViewModel =
            ViewModelProvider(this).get(WordOfDayViewModel::class.java)

        _binding = FragmentWordOfDayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val wordTextView: TextView = binding.wordOfTheDayTextView
        val wordDefinitionTextView: TextView = binding.wordOfTheDayDefinitionTextView
        val synonymTextView: TextView = binding.wordOfTheDaySynonymsTextView
        wordOfDayViewModel.text.observe(viewLifecycleOwner, Observer {
            wordTextView.text = it
            wordDefinitionTextView.text = it
            synonymTextView.text = it
        })

        // HTTP Client for request
        val client = OkHttpClient()

        // Request for the random word that will serve as the Word of the Day
        val randomRequest = Request.Builder()
            .url(WORDSAPI_PREREQUEST_STRING + WORDSAPI_RANDOM_WORD_STRING)
            .get()
            .addHeader(HOST_TITLE, HOST)
            .addHeader(API_KEY_TITLE, API_KEY)
            .build()

        // Response received by the HTTP request through a callback
        client.newCall(randomRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.v("RESPONSE", "Error!")
            } // End of method onFailure

            override fun onResponse(call: Call, response: Response) {
                // JSON string as the response of the HTTP request
                val jsonString = response.body?.string()
                Log.v("RANDOM_RESPONSE", jsonString.toString())

                // Using Moshi to parse the JSON string along with the respective data class
                val moshi: Moshi = Moshi.Builder().build()
                val adapter: JsonAdapter<RandomWord> = moshi.adapter(RandomWord::class.java)
                val wordResponse = adapter.fromJson(jsonString.toString())

                // Getting individual entries from the parsed object as well as preparing
                // them for updating the TextViews
                if (wordResponse != null) {
                    val randomWord = wordResponse.word

                    getWordDefinitions(client, randomWord, wordTextView, wordDefinitionTextView)
                    getWordSynonyms(client, randomWord, synonymTextView)
                }
            } // End of method onResponse
        }) // End of randomRequest callback

        return root
    } // End of method onCreateView

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    } // End of method onDestroyView

    /*
     * Method name: getWordDefinitions
     * Description: Requests the definition of the argument word by sending an
     *              HTTP request from WordsAPI. When it receives the response from WordsAPI,
     *              it parses it by using Moshi and user-defined data classes in this project.
     * Arguments:   client: The HTTP client that will be used to send the HTTP request.
     *              word: The word to search for.
     *              wordTextView: The TextView reference to update with the argument 'word'.
     *              wordDefinitionTextView: The TextView reference to update with the word's
     *                                      definition.
     *
     */
    fun getWordDefinitions(client: OkHttpClient,
                           word: String,
                           wordTextView: TextView,
                           wordDefinitionTextView: TextView) {
        // Revised word to account for spaces when making the HTTP request
        var revisedWord = word.replace(" ", "%20")

        // Request for the definition of the word. This too makes sure the word is returned
        // by requesting the word itself
        val definitionRequest = Request.Builder()
            .url(WORDSAPI_PREREQUEST_STRING + revisedWord + WORDSAPI_DEFINITIONS_STRING)
            .get()
            .addHeader(HOST_TITLE, HOST)
            .addHeader(API_KEY_TITLE, API_KEY)
            .build()

        // Response received by the HTTP request through a callback
        client.newCall(definitionRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.v("RESPONSE", "Error!")
            } // End of method onFailure

            override fun onResponse(call: Call, response: Response) {
                // JSON string as the response of the HTTP request
                val jsonString = response.body?.string()
                Log.v("DEF_RESPONSE", jsonString.toString())

                // Using Moshi to parse the JSON string along with the respective data class
                val moshi: Moshi = Moshi.Builder().build()
                val adapter: JsonAdapter<WordDefinition> = moshi.adapter(WordDefinition::class.java)
                val wordResponse = adapter.fromJson(jsonString.toString())

                // Getting individual entries from the parsed object as well as preparing
                // them for updating the TextViews
                if (wordResponse != null) {
                    val wordOfTheDay = wordResponse.word

                    var wordDefinition = ""
                    if (!wordResponse.definitions.isEmpty())
                        wordDefinition = "\"" + wordResponse.definitions[0].definition + "\""

                    activity?.runOnUiThread( {
                        wordTextView.setText(wordOfTheDay)
                        wordDefinitionTextView.setText(wordDefinition)
                    })
                }
            } // End of method onResponse
        }) // End of definitionRequest callback
    } // End of method getWordDefinitions

    /*
     * Method name: searchWord
     * Description: Requests the definition and synonyms of the argument word by sending an
     *              HTTP request from WordsAPI. When it receives the response from WordsAPI,
     *              it parses it by using Moshi and user-defined data classes in this project.
     * Arguments:   client: The HTTP client that will be used to send the HTTP request.
     *              word: The word to search for.
     *              synonymTextView: The TextView reference to update with the word's
     *                               list of synonyms.
     *
     */
    fun getWordSynonyms(client: OkHttpClient,
                    word: String,
                    synonymTextView: TextView) {
        // Revised word to account for spaces when making the HTTP request
        var revisedWord = word.replace(" ", "%20")

        // Request for the synonyms of the word. This too makes sure the word is returned
        // by requesting the word itself
        val synonymsRequest = Request.Builder()
            .url(WORDSAPI_PREREQUEST_STRING + revisedWord + WORDSAPI_SYNONYMS_STRING)
            .get()
            .addHeader(HOST_TITLE, HOST)
            .addHeader(API_KEY_TITLE, API_KEY)
            .build()

        // Response received by the HTTP request through a callback
        client.newCall(synonymsRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.v("SYN_RESPONSE", "Error!")
            } // End of method onFailure

            override fun onResponse(call: Call, response: Response) {
                // JSON string as the response of the HTTP request
                val jsonString = response.body?.string()
                Log.v("SYN_RESPONSE", jsonString.toString())

                // Using Moshi to parse the JSON string along with the respective data class
                val moshi: Moshi = Moshi.Builder().build()
                val adapter: JsonAdapter<WordSynonyms> = moshi.adapter(WordSynonyms::class.java)
                val wordResponse = adapter.fromJson(jsonString.toString())

                // Getting individual entries from the parsed object as well as preparing
                // them for updating the TextViews
                if (wordResponse != null) {
                    var synonyms = wordResponse.synonyms.toString()

                    synonyms = synonyms.replace("[", "")
                    synonyms = synonyms.replace("]", "")

                    activity?.runOnUiThread( {
                        synonymTextView.setText(synonyms)
                    })
                }
            } // End of method onResponse
        }) // End of synonymsRequest callback
    } // End of method getWordSynonyms
}