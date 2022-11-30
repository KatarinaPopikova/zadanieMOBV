package sk.stu.fei.mobv.ui.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import sk.stu.fei.mobv.domain.Bar
import sk.stu.fei.mobv.repository.Repository

class BarDetailViewModel(private val repository: Repository) : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading


    private val _bar = MutableLiveData<Bar?>(null)
    val bar: LiveData<Bar?>
        get() = _bar


    fun loadBar(id: Long) {
        viewModelScope.launch {
            _loading.postValue(true)
            repository.refreshBarList { _message.postValue(it) }
            val tagBar: Bar? = repository.getTagBar(id) { _message.postValue(it) }
            tagBar?.let {
                it.usersCount = repository.getBarUserCount(id)
            }
            _bar.postValue(tagBar)
            _loading.postValue(false)
        }
    }
}