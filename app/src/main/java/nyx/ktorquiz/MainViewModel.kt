package nyx.ktorquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import nyx.ktorquiz.Status.Loading
import nyx.ktorquiz.data.Repository

class MainViewModel : ViewModel() {

    var result = MutableStateFlow<Status<Any>>(Loading())

    init {
        getQuestions()
    }

    private fun getQuestions() {
        viewModelScope.launch {
            // delay(2500) //artificial delay
            result.value = Repository.getQuestions()
        }
    }
}