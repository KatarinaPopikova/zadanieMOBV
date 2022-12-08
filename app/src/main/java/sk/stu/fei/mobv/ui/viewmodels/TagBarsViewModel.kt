package sk.stu.fei.mobv.ui.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import sk.stu.fei.mobv.domain.Bar
import sk.stu.fei.mobv.domain.MyLocation
import sk.stu.fei.mobv.repository.Repository

class TagBarsViewModel(private val repository: Repository) : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    val loading = MutableLiveData(false)

    val myLocation = MutableLiveData<MyLocation>(null)
    val myBar = MutableLiveData<Bar>(null)

    private val _checkedIn = MutableLiveData(false)
    val checkedIn: LiveData<Boolean>
        get() = _checkedIn


    val bars: LiveData<List<Bar>> = myLocation.switchMap {
        liveData {
            loading.postValue(true)
            it?.let { ml ->
                val bars =
                    repository.getNearbyBars(ml.latitude, ml.longitude) { _message.postValue(it) }
                emit(bars)
                if (myBar.value == null) {
                    myBar.postValue(bars.firstOrNull())
                }
            } ?: emit(listOf())
            loading.postValue(false)
        }
    }

    fun checkMe() {
        viewModelScope.launch {
            loading.postValue(true)
            myBar.value?.let { bar ->
                repository.checkInBar(
                    bar,
                    { _message.postValue(it) },
                    { _checkedIn.postValue(it) })
            }
            loading.postValue(false)
        }
    }
}