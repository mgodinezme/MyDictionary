/*
 * Filename: DictionaryViewModel.kt
 * Description: View Model managing the logic behind Dictionary Fragment's UI.
 *
 */

package com.mgodinez.mydictionary.ui.dictionary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DictionaryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply { }

    val text: LiveData<String> = _text

} // End of class DictionaryViewModel