package com.dljonesapps.sparqchallenge.data.repository

import com.dljonesapps.sparqchallenge.data.db.PokemonEntity
import com.dljonesapps.sparqchallenge.data.model.PokemonDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Test implementation of a repository for testing the PokemonListViewModel
 * without relying on the actual PokemonRepository implementation
 */
class TestPokemonRepository {
    private var pokemonListResult: Result<List<PokemonEntity>> = Result.success(emptyList())
    private var pokemonDetailResult: Result<PokemonDetail> = Result.success(
        PokemonDetail(
            id = 0,
            name = "",
            height = 0,
            weight = 0,
            types = emptyList(),
            stats = emptyMap(),
            abilities = emptyList(),
            imageUrl = ""
        )
    )
    
    fun setPokemonListResult(result: Result<List<PokemonEntity>>) {
        pokemonListResult = result
    }
    
    fun setPokemonDetailResult(result: Result<PokemonDetail>) {
        pokemonDetailResult = result
    }
    
    fun getPokemonList(): Flow<Result<List<PokemonEntity>>> {
        return flowOf(pokemonListResult)
    }
    
    fun getPokemonDetail(url: String): Flow<Result<PokemonDetail>> {
        return flowOf(pokemonDetailResult)
    }
}
