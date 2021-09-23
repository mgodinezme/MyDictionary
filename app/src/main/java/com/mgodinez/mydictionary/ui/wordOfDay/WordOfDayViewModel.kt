/*
 * Filename: WordOfDayViewModel.kt
 * Description: View Model managing the logic behind WordOfDayFragment's UI.
 *
 */

package com.mgodinez.mydictionary.ui.wordOfDay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WordOfDayViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply { }

    val text: LiveData<String> = _text

} // End of class WordOfDayViewModel