package com.example.thomas_laughingman.pokedex_master.model

class PokemonAbilities(
    val id : Long,
    val ability: String,
    val slot: Int,
    val isHidden: Int
)
{
    companion object {
        val TABLE_NAME = "pokemon"
        val pokemon_id_column = "id"
        val pokemon_isMega_column = "is_mega"
    }
}