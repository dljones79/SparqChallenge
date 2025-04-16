package com.dljonesapps.sparqchallenge.ui.state

import com.dljonesapps.sparqchallenge.data.model.PokemonDetail

data class PokemonDetailUiState(
    val pokemonDetail: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
