package sk.stu.fei.mobv.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stu.fei.mobv.domain.Friend
import sk.stu.fei.mobv.repository.Repository

class FriendsViewModel(private val repository: Repository) : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    val loading = MutableLiveData(false)

    private val _friends = MutableLiveData<List<Friend>>(null)
    val friends: LiveData<List<Friend>?> = _friends

    fun addFriend(friendName: String) {
        loading.postValue(true)
        viewModelScope.launch {
            repository.addFriend(friendName,
                { _message.postValue(it) },
                { _message.postValue(it) })
        }
        loading.postValue(false)
    }

    fun loadFriends() {
        viewModelScope.launch {
            loading.postValue(true)
            _friends.postValue(repository.getFriends { _message.postValue(it) })
            loading.postValue(false)
        }
    }

}