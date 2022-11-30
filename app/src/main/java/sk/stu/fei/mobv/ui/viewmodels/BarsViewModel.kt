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

    private var barsSort: MutableLiveData<BarsSort> = MutableLiveData(BarsSort.NAME_ASC)

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    val bars: LiveData<List<Bar>?> by lazy {
        refreshData()
        return@lazy Transformations.switchMap(barsSort) { sort ->
            when (sort) {
                BarsSort.NAME_ASC -> repository.getBarsByNameAsc()
                BarsSort.NAME_DESC -> repository.getBarsByNameDesc()
                BarsSort.DIST_ASC -> repository.getBarsByNameAsc()
                BarsSort.DIST_DESC -> repository.getBarsByNameAsc()
                BarsSort.VISIT_ASC -> repository.getBarsByUsersCountAsc()
                else -> repository.getBarsByUsersCountDesc()
            }
        }
    }

    fun sortBy(sortType: BarsSort) {
        barsSort.value = sortType
    }

    fun refreshData() {
        viewModelScope.launch {
            _loading.postValue(true)
            repository.refreshBarList { _message.postValue(it) }
            _loading.postValue(false)
        }
    }

}