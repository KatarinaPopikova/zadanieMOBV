package sk.stu.fei.mobv.ui.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sk.stu.fei.mobv.repository.Repository
import sk.stu.fei.mobv.ui.viewmodels.AuthenticationViewModel

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthenticationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthenticationViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}