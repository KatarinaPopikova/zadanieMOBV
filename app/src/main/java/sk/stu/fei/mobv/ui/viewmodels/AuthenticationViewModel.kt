package sk.stu.fei.mobv.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stu.fei.mobv.domain.User
import sk.stu.fei.mobv.network.dtos.asDomainModel
import sk.stu.fei.mobv.repository.Repository

class AuthenticationViewModel(private val repository: Repository): ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    val user = MutableLiveData<User>(null)

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    fun login(name: String, password: String){
        viewModelScope.launch {
            _loading.postValue(true)
            repository.loginUser(
                name,password,
                { _message.postValue(it) },
                { user.postValue(it?.asDomainModel()) }
            )
            _loading.postValue(false)
        }
    }

    fun signup(name: String, password: String){
        viewModelScope.launch {
            _loading.postValue(true)
            repository.registerUser(
                name,password,
                { _message.postValue(it) },
                { user.postValue(it?.asDomainModel()) }
            )
            _loading.postValue(false)
        }
    }

}