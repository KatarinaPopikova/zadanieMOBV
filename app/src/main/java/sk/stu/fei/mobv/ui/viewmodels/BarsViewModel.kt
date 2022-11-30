package sk.stu.fei.mobv.ui.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import sk.stu.fei.mobv.domain.Bar
import sk.stu.fei.mobv.helpers.BarsSort
import sk.stu.fei.mobv.repository.Repository

class BarsViewModel(private val repository: Repository) : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    private var _barsSort: MutableLiveData<BarsSort> = MutableLiveData(BarsSort.NAME_ASC)
    val isSortAsc: LiveData<BarsSort>
        get() = _barsSort

    val loading = MutableLiveData(false)

    val bars: LiveData<List<Bar>?> = Transformations.switchMap(_barsSort) { sort ->
        when (sort) {
            BarsSort.NAME_ASC -> repository.getBarsByNameAsc()
            BarsSort.NAME_DESC -> repository.getBarsByNameDesc()
            BarsSort.DIST_ASC -> repository.getBarsByNameAsc()
            BarsSort.DIST_DESC -> repository.getBarsByNameAsc()
            BarsSort.VISIT_ASC -> repository.getBarsByUsersCountAsc()
            else -> repository.getBarsByUsersCountDesc()
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            loading.postValue(true)
            repository.refreshBarList { _message.postValue(it) }
            loading.postValue(false)
        }
    }

}