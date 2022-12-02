package sk.stu.fei.mobv.ui.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sk.stu.fei.mobv.repository.Repository
import sk.stu.fei.mobv.ui.viewmodels.*

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthenticationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthenticationViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(BarsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BarsViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(BarDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BarDetailViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(TagBarsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TagBarsViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(FriendsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendsViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}