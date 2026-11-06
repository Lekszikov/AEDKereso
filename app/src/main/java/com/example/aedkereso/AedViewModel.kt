package com.example.aedkereso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aedkereso.data.AedRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AedViewModel(private val repository: AedRepository) : ViewModel() {

    private val _rawElements = MutableStateFlow<List<AedElement>>(emptyList())

    // Keresőszó állapota
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Kombinált lista: szűri a keresőszóra és rendezi a távolság alapján
    val aedItems: StateFlow<List<AedUiItem>> = combine(_rawElements, _searchQuery) { elements, query ->
        val filtered = if (query.isEmpty()) {
            elements
        } else {
            elements.filter {
                (it.helyszin?.contains(query, ignoreCase = true) == true) ||
                        (it.telepules?.contains(query, ignoreCase = true) == true) ||
                        (it.cim?.contains(query, ignoreCase = true) == true)
            }
        }

        // Átalakítjuk UI elemekké (Itt fix teszt-távolságot adunk, amit a GPS-ből lehetne finomítani)
        filtered.map { element ->
            AedUiItem(element = element, distanceInMeters = null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadDefibrillators()
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun loadDefibrillators() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _rawElements.value = repository.getDefibrillators()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}