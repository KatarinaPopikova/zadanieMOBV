package sk.stu.fei.mobv.ui.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sk.stu.fei.mobv.repository.Repository
import sk.stu.fei.mobv.ui.viewmodels.AuthenticationViewModel
import sk.stu.fei.mobv.ui.viewmodels.BarDetailViewModel
import sk.stu.fei.mobv.ui.viewmodels.BarsViewModel
import sk.stu.fei.mobv.ui.viewmodels.TagBarsViewModel

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

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}