package uz.moneymanager.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.moneymanager.R
import uz.moneymanager.data.local.CategoryWithUsage
import uz.moneymanager.data.repository.CategoryRepository
import uz.moneymanager.domain.DomainException
import javax.inject.Inject

sealed interface CategoryEvent {
    data object Success : CategoryEvent
    data class Error(val messageRes: Int, val args: List<Any> = emptyList()) : CategoryEvent
}

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    val categories: StateFlow<List<CategoryWithUsage>> = categoryRepository.observeWithUsage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _events = MutableSharedFlow<CategoryEvent>()
    val events: SharedFlow<CategoryEvent> = _events.asSharedFlow()

    fun renameCategory(id: Long, newName: String) {
        viewModelScope.launch {
            try {
                categoryRepository.rename(id, newName)
                _events.emit(CategoryEvent.Success)
            } catch (e: DomainException) {
                _events.emit(CategoryEvent.Error(e.messageRes, e.args.toList()))
            }
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            try {
                categoryRepository.delete(id)
                _events.emit(CategoryEvent.Success)
            } catch (e: DomainException) {
                _events.emit(CategoryEvent.Error(e.messageRes, e.args.toList()))
            }
        }
    }
}
