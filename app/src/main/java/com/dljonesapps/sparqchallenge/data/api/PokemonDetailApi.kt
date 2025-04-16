package com.dljonesapps.sparqchallenge.data.api

import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonDetailApi {
    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(@Path("id") id: String): PokemonDetailResponse
}

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<PokemonTypeSlot>,
    val stats: List<PokemonStatSlot>,
    val abilities: List<PokemonAbilitySlot>,
    val sprites: PokemonSprites
)

data class PokemonTypeSlot(
    val slot: Int,
    val type: NamedApiResource
)

data class PokemonStatSlot(
    val base_stat: Int,
    val effort: Int,
    val stat: NamedApiResource
)

data class PokemonAbilitySlot(
    val ability: NamedApiResource,
    val is_hidden: Boolean,
    val slot: Int
)

data class NamedApiResource(
    val name: String,
    val url: String
)

data class PokemonSprites(
    val front_default: String?,
    val front_shiny: String?,
    val other: OtherSprites?
)

data class OtherSprites(
    val dream_world: DreamWorldSprites?,
    val home: HomeSprites?,
    val official_artwork: OfficialArtworkSprites?
) {
    val official_artwork_front_default: String?
        get() = official_artwork?.front_default
}

data class DreamWorldSprites(
    val front_default: String?
)

data class HomeSprites(
    val front_default: String?
)

data class OfficialArtworkSprites(
    val front_default: String?
)
