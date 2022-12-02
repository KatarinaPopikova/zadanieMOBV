package sk.stu.fei.mobv.ui.viewmodels

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import sk.stu.fei.mobv.domain.Bar
import sk.stu.fei.mobv.domain.MyLocation
import sk.stu.fei.mobv.helpers.BarsSort
import sk.stu.fei.mobv.repository.Repository

class BarsViewModel(private val repository: Repository) : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    private var barsSort: MutableLiveData<BarsSort> = MutableLiveData(BarsSort.NAME_ASC)

    val loading = MutableLiveData(false)

    val myLocation = MutableLiveData<MyLocation>(null)

    val bars: LiveData<List<Bar>?> by lazy {
        refreshData()
        return@lazy Transformations.switchMap(barsSort) { sort ->
            when (sort) {
                BarsSort.NAME_ASC -> repository.getBarsByNameAsc()
                BarsSort.NAME_DESC -> repository.getBarsByNameDesc()
                BarsSort.DIST_ASC -> repository.getBarsByDistanceAsc(myLocation.value)
                BarsSort.DIST_DESC -> repository.getBarsByDistanceDesc(myLocation.value)
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
            loading.postValue(true)
            repository.refreshBarList { _message.postValue(it) }
            loading.postValue(false)
        }
    }

}