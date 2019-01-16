package com.example.thomas_laughingman.pokedex_master.model

data class Pokemon_Master_Trainer_Moves ( var id: Long,
                                    var pokemon_id: Long,
                                    var move: String)
{
    companion object {
        val TABLE_NAME = "master_trainer_pokemon_moves"
        val pokemon_id_column = "pokemon_id"
    }
}