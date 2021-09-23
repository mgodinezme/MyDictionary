/*
 * Filename: DictionaryFragment.kt
 * Description: Fragment class managing the content of the Dictionary section of this project.
 *              Fragment is activated when its respective bottom tab is clicked/pressed.
 *
 */

package com.mgodinez.mydictionary.ui.dictionary

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mgodinez.mydictionary.databinding.FragmentDictionaryBinding
import com.mgodinez.mydictionary.model.HTTPData.Companion.API_KEY
import com.mgodinez.mydictionary.model.HTTPData.Companion.API_KEY_TITLE
import com.mgodinez.mydictionary.model.HTTPData.Companion.HOST
import com.mgodinez.mydictionary.model.HTTPData.Companion.HOST_TITLE
import com.mgodinez.mydictionary.model.HTTPData.Companion.WORDSAPI_DEFINITIONS_STRING
import com.mgodinez.mydictionary.model.HTTPData.Companion.WORDSAPI_PREREQUEST_STRING
import com.mgodinez.mydictionary.model.HTTPData.Companion.WORDSAPI_SYNONYMS_STRING
import com.mgodinez.mydictionary.model.WordDefinition
import com.mgodinez.mydictionary.model.WordSynonyms
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.*
import okio.IOException

class DictionaryFragment : Fragment() {

    private lateinit var dictionaryViewModel: DictionaryViewModel
    private var _binding: FragmentDictionaryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dictionaryViewModel =
            ViewModelProvider(this).get(DictionaryViewModel::class.java)

        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)

        val searchWordEditText: EditText = binding.searchWordEditText
        val wordTextView: TextView = binding.resultWordTextView
        val wordDefinitionTextView: TextView = binding.wordDefinitionTextView
        val synonymsTextView: TextView = binding.wordSynonymsTextView
        dictionaryViewModel.text.observe(viewLifecycleOwner, Observer {
            searchWordEditText.text = Editable.Factory.getInstance().newEditable(it)
            wordTextView.text = it
            wordDefinitionTextView.text = it
            synonymsTextView.text = it
        })

        // Search button listener:
        // 1) Get text from EditText
        // 2) Check word text string is not null
        // 3) Pass word to be queried for HTTP request
        binding.searchButton.setOnClickListener {
            val wordToSearch = searchWordEditText.text.toString()

            if (wordToSearch != null)
                searchWord(wordToSearch, wordTextView, wordDefinitionTextView, synonymsTextView)
            else
                Toast.makeText(activity, "Invalid Input", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    } // End of method onCreateView

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    } // End if method onDestroyView

    /*
     * Method name: searchWord
     * Description: Requests the definition and synonyms of the argument word by sending an
     *              HTTP request from WordsAPI. When it receives the response from WordsAPI,
     *              it parses it by using Moshi and user-defined data classes in this project.
     * Arguments:   word: The word to search for.
     *              wordTextView: The TextView reference to update with the argument 'word'.
     *              wordDefinitionTextView: The TextView reference to update with the word's
     *                                      definition.
     *              wordSynonymsTextView: The TextView reference to update with the word's
     *                                    list of synonyms.
     *
     */
    fun searchWord(word: String,
                   wordTextView: TextView,
                   wordDefinitionTextView: TextView,
                   wordSynonymsTextView: TextView) {
        // HTTP Client for request
        val client = OkHttpClient()

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
                    val word = wordResponse.word
                    val wordDefinition = "\"" + wordResponse.definitions[0].definition + "\""

                    activity?.runOnUiThread( {
                        wordTextView.setText(word)
                        wordDefinitionTextView.setText(wordDefinition)
                    })
                }
            } // End of method onResponse
        }) // End of definitionRequest callback

        // Request for the synonyms of the word
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
                Log.v("RESPONSE", jsonString.toString())

                // Using Moshi to parse the JSON string along with the respective data class
                val moshi: Moshi = Moshi.Builder().build()
                val adapter: JsonAdapter<WordSynonyms> = moshi.adapter(WordSynonyms::class.java)
                val wordResponse = adapter.fromJson(jsonString.toString())

                // Getting individual entries from the parsed object as well as preparing
                // them for updating the TextViews
                if (wordResponse != null) {
                    val wordSynonyms = wordResponse.synonyms[0]

                    activity?.runOnUiThread( {
                        wordSynonymsTextView.setText(wordSynonyms)
                    })
                }
            } // End of method onResponse
        }) // End of synonymsRequest callback
    } // End of method searchWord

} // End of class DictionaryFragment