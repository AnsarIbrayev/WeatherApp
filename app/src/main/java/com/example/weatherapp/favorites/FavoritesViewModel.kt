package com.example.weatherapp.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val loading: Boolean = true,
    val uid: String = "",
    val items: List<FavoriteItem> = emptyList(),
    val error: String? = null
)

class FavoritesViewModel(
    private val repo: FavoritesRepository = FavoritesRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesUiState())
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    fun start() {
        if (_state.value.uid.isNotBlank()) return
        viewModelScope.launch {
            try {
                val uid = repo.ensureAnonUid()
                _state.update { it.copy(uid = uid, loading = false) }

                repo.observe(uid).collect { list ->
                    _state.update { it.copy(items = list, error = null) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun add(title: String, note: String) {
        val uid = _state.value.uid
        if (uid.isBlank()) return
        repo.add(uid, title, note)
    }

    fun update(id: String, title: String, note: String) {
        val uid = _state.value.uid
        if (uid.isBlank()) return
        repo.update(uid, id, title, note)
    }

    fun delete(id: String) {
        val uid = _state.value.uid
        if (uid.isBlank()) return
        repo.delete(uid, id)
    }
}
