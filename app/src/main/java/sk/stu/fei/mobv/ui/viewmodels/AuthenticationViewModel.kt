package sk.stu.fei.mobv.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stu.fei.mobv.domain.User
import sk.stu.fei.mobv.network.dtos.asDomainModel
import sk.stu.fei.mobv.repository.Repository
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

class AuthenticationViewModel(private val repository: Repository): ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    val user = MutableLiveData<User>(null)

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    fun login(name: String, password: String){
        val hashedPassword = hashPassword(password) ?: return

        viewModelScope.launch {
            _loading.postValue(true)
            repository.loginUser(
                name,hashedPassword,
                { _message.postValue(it) },
                { user.postValue(it?.asDomainModel()) }
            )
            _loading.postValue(false)
        }
    }

    fun signup(name: String, password: String){
        val hashedPassword = hashPassword(password) ?: return
        viewModelScope.launch {
            _loading.postValue(true)
            repository.registerUser(
                name,hashedPassword,
                { _message.postValue(it) },
                { user.postValue(it?.asDomainModel()) }
            )
            _loading.postValue(false)
        }
    }

    private fun hashPassword(passwordToHash: String): String? {
        val salt = "salt"

        try {
            val md: MessageDigest = MessageDigest.getInstance("SHA-512")
            md.update(salt.toByteArray())
            val bytes: ByteArray = md.digest(passwordToHash.toByteArray())
            val sb = StringBuilder()
            for (i in bytes.indices) {
                sb.append(
                    ((bytes[i] and 0xff.toByte()) + 0x100).toString(16)
                        .substring(1)
                )
            }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            _message.value = "Problém ohľadom hesla."
            return null
        }
    }
}