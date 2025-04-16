package com.dljonesapps.sparqchallenge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dljonesapps.sparqchallenge.data.repository.PokemonRepository
import com.dljonesapps.sparqchallenge.ui.state.PokemonListUiState
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class PokemonListViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonListUiState())
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    init {
        loadPokemon()
    }

    fun loadPokemon() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                repository.getPokemonList()
                    .collect { result ->
                        result.fold(
                            onSuccess = { pokemon ->
                                _uiState.update { state ->
                                    state.copy(
                                        pokemon = (state.pokemon + pokemon).toPersistentList(),
                                        isLoading = false,
                                        error = null
                                    )
                                }
                            },
                            onFailure = { throwable ->
                                _uiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        error = throwable.message
                                    )
                                }
                            }
                        )
                    }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
}
