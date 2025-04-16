package com.dljonesapps.sparqchallenge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dljonesapps.sparqchallenge.data.db.PokemonEntity
import com.dljonesapps.sparqchallenge.data.model.PokemonDetail
import com.dljonesapps.sparqchallenge.data.repository.TestPokemonRepository
import com.dljonesapps.sparqchallenge.ui.state.PokemonDetailUiState
import com.dljonesapps.sparqchallenge.ui.state.PokemonListUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {
    private lateinit var viewModel: TestPokemonListViewModel
    private lateinit var testRepository: TestPokemonRepository
    private val testDispatcher = StandardTestDispatcher()
    
    // Test data
    private val mockPokemon1 = PokemonEntity("bulbasaur", "url1", 0)
    private val mockPokemon2 = PokemonEntity("charmander", "url2", 1)
    private val mockPokemons = listOf(mockPokemon1, mockPokemon2)
    private val mockPokemonDetail = PokemonDetail(
        id = 1,
        name = "bulbasaur",
        height = 7,
        weight = 69,
        types = listOf("grass", "poison"),
        stats = mapOf("hp" to 45, "attack" to 49),
        abilities = listOf("overgrow"),
        imageUrl = "https://example.com/bulbasaur.png"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        testRepository = TestPokemonRepository()
        viewModel = TestPokemonListViewModel(testRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPokemon updates state with new pokemon on success`() = runTest {
        // Given
        testRepository.setPokemonListResult(Result.success(mockPokemons))

        // When
        viewModel.loadPokemon()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(mockPokemons, viewModel.uiState.value.pokemon.toList())
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadPokemon updates state with error on failure`() = runTest {
        // Given
        val errorMessage = "Network error"
        testRepository.setPokemonListResult(Result.failure(Exception(errorMessage)))

        // When
        viewModel.loadPokemon()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(persistentListOf<PokemonEntity>(), viewModel.uiState.value.pokemon)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(errorMessage, viewModel.uiState.value.error)
    }
    
    @Test
    fun `selectPokemon sets detail visibility and loads pokemon detail`() = runTest {
        // Given
        testRepository.setPokemonDetailResult(Result.success(mockPokemonDetail))
        
        // When
        viewModel.selectPokemon(mockPokemon1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(viewModel.isDetailVisible.value)
        assertEquals(mockPokemonDetail, viewModel.detailUiState.value.pokemonDetail)
    }
    
    @Test
    fun `dismissDetail hides the detail view`() = runTest {
        // Given
        testRepository.setPokemonDetailResult(Result.success(mockPokemonDetail))
        viewModel.selectPokemon(mockPokemon1) // First show the detail
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.isDetailVisible.value)
        
        // When
        viewModel.dismissDetail()
        
        // Then
        assertFalse(viewModel.isDetailVisible.value)
    }
    
    @Test
    fun `loadPokemonDetail updates detail state with error on failure`() = runTest {
        // Given
        val errorMessage = "Detail fetch error"
        testRepository.setPokemonDetailResult(Result.failure(Exception(errorMessage)))
        
        // When
        viewModel.selectPokemon(mockPokemon1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.detailUiState.value.isLoading)
        assertEquals(errorMessage, viewModel.detailUiState.value.error)
        assertNull(viewModel.detailUiState.value.pokemonDetail)
    }
    
    /**
     * Test-specific implementation of PokemonListViewModel that uses TestPokemonRepository
     */
    private class TestPokemonListViewModel(private val repository: TestPokemonRepository) : ViewModel() {
        private val _uiState = MutableStateFlow(PokemonListUiState())
        val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()
        
        private val _detailUiState = MutableStateFlow(PokemonDetailUiState())
        val detailUiState: StateFlow<PokemonDetailUiState> = _detailUiState.asStateFlow()
        
        private val _isDetailVisible = MutableStateFlow(false)
        val isDetailVisible: StateFlow<Boolean> = _isDetailVisible.asStateFlow()
        
        fun loadPokemon() {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                
                repository.getPokemonList().collect { result ->
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
            }
        }
        
        fun selectPokemon(pokemon: PokemonEntity) {
            _isDetailVisible.value = true
            loadPokemonDetail(pokemon.url)
        }
        
        fun dismissDetail() {
            _isDetailVisible.value = false
        }
        
        private fun loadPokemonDetail(url: String) {
            viewModelScope.launch {
                _detailUiState.update { it.copy(isLoading = true) }
                
                repository.getPokemonDetail(url).collect { result ->
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
            }
        }
    }
}
