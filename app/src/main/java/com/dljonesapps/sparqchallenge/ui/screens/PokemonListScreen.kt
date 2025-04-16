package com.dljonesapps.sparqchallenge.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.dljonesapps.sparqchallenge.ui.components.InfiniteScrollList
import com.dljonesapps.sparqchallenge.ui.components.PokemonCard
import com.dljonesapps.sparqchallenge.ui.components.PokemonDetailCard
import com.dljonesapps.sparqchallenge.ui.viewmodel.PokemonListViewModel
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Alignment

@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val detailUiState by viewModel.detailUiState.collectAsState()
    val isDetailVisible by viewModel.isDetailVisible.collectAsState()
    val listState = rememberLazyListState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Pokemon list
            InfiniteScrollList(
                items = uiState.pokemon,
                itemContent = { pokemon ->
                    PokemonCard(
                        pokemon = pokemon,
                        modifier = Modifier,
                        onClick = { viewModel.selectPokemon(pokemon) }
                    )
                },
                isLoading = uiState.isLoading,
                loadMoreItems = viewModel::loadPokemon,
                listState = listState,
                modifier = Modifier.fillMaxSize()
            )
            
            // Pokemon detail card
            val currentDetail = detailUiState.pokemonDetail
            if (currentDetail != null) {
                PokemonDetailCard(
                    pokemonDetail = currentDetail,
                    isVisible = isDetailVisible,
                    onDismiss = { viewModel.dismissDetail() }
                )
            }
            
            // Loading indicator for detail
            if (detailUiState.isLoading && isDetailVisible) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            // Error message for detail
            detailUiState.error?.let { error ->
                if (isDetailVisible) {
                    Snackbar(
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Text(text = "Error: $error")
                    }
                }
            }
        }
    }
}
