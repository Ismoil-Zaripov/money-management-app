package uz.moneymanager.feature.categories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.moneymanager.data.repository.CategoryRepository
import uz.moneymanager.domain.DomainException
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    val uiState: StateFlow<CategoriesUiState> = categoryRepository.observeWithUsage()
        .map { CategoriesUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoriesUiState())

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
