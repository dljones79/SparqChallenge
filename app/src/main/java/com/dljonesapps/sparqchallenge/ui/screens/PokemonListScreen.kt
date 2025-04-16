package com.dljonesapps.sparqchallenge.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.dljonesapps.sparqchallenge.ui.components.InfiniteScrollList
import com.dljonesapps.sparqchallenge.ui.components.PokemonCard
import com.dljonesapps.sparqchallenge.ui.viewmodel.PokemonListViewModel
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState

@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        InfiniteScrollList(
            items = uiState.pokemon,
            itemContent = { pokemon ->
                PokemonCard(
                    pokemon = pokemon,
                    modifier = Modifier
                )
            },
            isLoading = uiState.isLoading,
            loadMoreItems = viewModel::loadPokemon,
            listState = listState,
            modifier = Modifier.fillMaxSize()
        )
    }
}
