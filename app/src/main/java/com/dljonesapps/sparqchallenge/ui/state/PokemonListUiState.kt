package com.dljonesapps.sparqchallenge.ui.state

import com.dljonesapps.sparqchallenge.data.db.PokemonEntity
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class PokemonListUiState(
    val pokemon: PersistentList<PokemonEntity> = persistentListOf(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

