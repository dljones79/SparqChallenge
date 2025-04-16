package com.dljonesapps.sparqchallenge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dljonesapps.sparqchallenge.data.db.PokemonEntity
import com.dljonesapps.sparqchallenge.data.model.PokemonDetail
import com.dljonesapps.sparqchallenge.data.repository.PokemonRepository
import com.dljonesapps.sparqchallenge.ui.state.PokemonDetailUiState
import com.dljonesapps.sparqchallenge.ui.state.PokemonListUiState
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class PokemonListViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonListUiState())
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()
    
    private val _detailUiState = MutableStateFlow(PokemonDetailUiState())
    val detailUiState: StateFlow<PokemonDetailUiState> = _detailUiState.asStateFlow()
    
    private val _selectedPokemon = MutableStateFlow<PokemonEntity?>(null)
    val selectedPokemon: StateFlow<PokemonEntity?> = _selectedPokemon.asStateFlow()
    
    private val _isDetailVisible = MutableStateFlow(false)
    val isDetailVisible: StateFlow<Boolean> = _isDetailVisible.asStateFlow()

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
    
    fun selectPokemon(pokemon: PokemonEntity) {
        _selectedPokemon.value = pokemon
        _isDetailVisible.value = true
        loadPokemonDetail(pokemon.url)
    }
    
    fun dismissDetail() {
        _isDetailVisible.value = false
    }
    
    private fun loadPokemonDetail(url: String) {
        viewModelScope.launch {
            try {
                _detailUiState.update { it.copy(isLoading = true) }
                
                repository.getPokemonDetail(url)
                    .collect { result ->
                        result.fold(
                            onSuccess = { pokemonDetail ->
                                _detailUiState.update { state ->
                                    state.copy(
                                        pokemonDetail = pokemonDetail,
                                        isLoading = false,
                                        error = null
                                    )
                                }
                            },
                            onFailure = { throwable ->
                                _detailUiState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        error = throwable.message
                                    )
                                }
                            }
                        )
                    }
            } catch (e: Exception) {
                _detailUiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
}
