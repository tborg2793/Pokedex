package com.example.thomas_laughingman.pokedex_master.model

data class Pokemon_Master_Trainer ( var id: Long,
                                    var pokemon_id: Long,
                                    var master_trainer_name: String,
                                    var pokemon_name: String,
                                    var level: Int,
                                    var location: String)
{
    companion object {
        val TABLE_NAME = "master_trainers"
        val pokemon_id_column = "pokemon_id"
    }
}