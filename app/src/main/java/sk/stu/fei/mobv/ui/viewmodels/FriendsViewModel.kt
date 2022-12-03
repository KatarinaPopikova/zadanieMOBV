package sk.stu.fei.mobv.ui.viewmodels

import androidx.lifecycle.*
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

    val myFriends: LiveData<List<Friend>> =
        liveData {
            loading.postValue(true)
            repository.refreshMyFriendsList { _message.postValue(it) }
            loading.postValue(false)
            emitSource(repository.getMyFriends())
        }

    fun addFriend(friendName: String) {
        loading.postValue(true)
        viewModelScope.launch {
            repository.addFriend(friendName,
                { _message.postValue(it) },
                { _message.postValue(it) })
        }
        loading.postValue(false)
    }

    fun deleteFriend(myFriend: Friend) {
        loading.postValue(true)
        viewModelScope.launch {
            repository.deleteMyFriend(myFriend,
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

    fun refreshMyFriends() {
        viewModelScope.launch {
            loading.postValue(true)
            repository.refreshMyFriendsList { _message.postValue(it) }
            loading.postValue(false)
        }
    }

}