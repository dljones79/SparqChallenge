package com.dljonesapps.sparqchallenge.ui.viewmodel

import com.dljonesapps.sparqchallenge.data.db.PokemonEntity
import com.dljonesapps.sparqchallenge.data.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {
    private lateinit var viewModel: PokemonListViewModel
    private val repository: PokemonRepository = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMorePokemon updates state with new pokemons on success`() = runTest {
        // Given
        val mockPokemons = listOf(
            PokemonEntity("bulbasaur", "url1"),
            PokemonEntity("charmander", "url2")
        )
        whenever(repository.getPokemonList(10, 0))
            .thenReturn(flowOf(Result.success(mockPokemons)))

        // When
        viewModel = PokemonListViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(mockPokemons, viewModel.uiState.value.pokemons)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.error)
    }

    @Test
    fun `loadMorePokemon updates state with error on failure`() = runTest {
        // Given
        val errorMessage = "Network error"
        whenever(repository.getPokemonList(10, 0))
            .thenReturn(flowOf(Result.failure(Exception(errorMessage))))

        // When
        viewModel = PokemonListViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(emptyList<PokemonEntity>(), viewModel.uiState.value.pokemons)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(errorMessage, viewModel.uiState.value.error)
    }
}
