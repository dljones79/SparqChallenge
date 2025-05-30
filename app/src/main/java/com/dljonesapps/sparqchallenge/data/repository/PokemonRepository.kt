package com.dljonesapps.sparqchallenge.data.repository

import android.net.Uri
import com.dljonesapps.sparqchallenge.data.api.PokemonApi
import com.dljonesapps.sparqchallenge.data.api.PokemonDetailApi
import com.dljonesapps.sparqchallenge.data.db.PokemonDao
import com.dljonesapps.sparqchallenge.data.db.PokemonEntity
import com.dljonesapps.sparqchallenge.data.model.PokemonDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PokemonRepository(
    private val api: PokemonApi,
    private val detailApi: PokemonDetailApi,
    private val dao: PokemonDao
) {
    // Local variable for pagination state
    private var offset: Int = 0
    private var limit: Int = 10

    /**
     * Parses the offset and limit parameters from the given "next" URL.
     * If the URL is null, it indicates there is no further data.
     *
     * @param nextUrl the URL string from the API response's "next" field.
     */
    private fun updatePagination(nextUrl: String?) {
        if (nextUrl == null) {
            // No more data: using a sentinel value for offset to indicate termination.
            offset = -1
            limit = 0
        } else {
            // Parse the query parameters from the URL.
            val uri = Uri.parse(nextUrl)
            offset = uri.getQueryParameter("offset")?.toIntOrNull() ?: offset
            limit = uri.getQueryParameter("limit")?.toIntOrNull() ?: limit
        }
    }

    fun getPokemonList(): Flow<Result<List<PokemonEntity>>> = flow {
        try {
            // Get from API
            val response = if (offset == -1) {
                null
            } else {
                api.getPokemonList(limit, offset)
            }

            if (response != null) {
                // Update offset and limit for next pagination request
                updatePagination(response.next)

                // Map and save results
                val pokemons = response.results.mapIndexed { index, pokemon -> 
                    PokemonEntity(
                        name = pokemon.name,
                        url = pokemon.url,
                        listPosition = offset + index
                    )
                }
                
                // Insert new items
                dao.insertAll(pokemons)
                
                // Return just the new items
                emit(Result.success(pokemons))
            } else {
                // No more results
                emit(Result.success(emptyList()))
            }
        } catch (e: Exception) {
            // On error, just emit the failure
            emit(Result.failure(e))
        }
    }
    
    fun getPokemonDetail(url: String): Flow<Result<PokemonDetail>> = flow {
        try {
            // Extract the Pokemon ID from the URL
            val pokemonId = url.split("/").dropLast(1).last()
            
            // Fetch the Pokemon details
            val response = detailApi.getPokemonDetail(pokemonId)
            
            // Map the response to our domain model
            val pokemonDetail = PokemonDetail(
                id = response.id,
                name = response.name,
                height = response.height,
                weight = response.weight,
                types = response.types.map { it.type.name },
                stats = response.stats.associate { it.stat.name to it.base_stat },
                abilities = response.abilities.map { it.ability.name },
                imageUrl = response.sprites.other?.official_artwork?.front_default 
                    ?: response.sprites.front_default
                    ?: "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$pokemonId.png"
            )
            
            emit(Result.success(pokemonDetail))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
