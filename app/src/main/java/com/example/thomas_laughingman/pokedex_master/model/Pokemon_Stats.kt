package com.example.thomas_laughingman.pokedex_master.model

data class Pokemon_Stats (var id: Long,
                          var weight: String,
                          var height: String,
                          var max_cp: Int,
                          var atk: Int,
                          var sp_atk: Int,
                          var def: Int,
                          var sp_def: Int,
                          var hp: Int,
                          var speed: Int,
                          var is_mega: Int)
{
    companion object {
        val TABLE_NAME = "pokemon_stats"
        val pokemon_id_column = "id"
        val pokemon_isMega_column = "is_mega"
    }
}