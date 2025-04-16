package com.dljonesapps.sparqchallenge.data.model

data class PokemonDetail(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<String>,
    val stats: Map<String, Int>,
    val abilities: List<String>,
    val imageUrl: String
)
