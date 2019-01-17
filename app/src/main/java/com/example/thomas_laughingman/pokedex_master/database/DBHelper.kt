package com.example.thomas_laughingman.pokedex_master.database

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.thomas_laughingman.pokedex_master.model.*
import org.jetbrains.anko.db.*
import java.io.File
import java.io.FileOutputStream

class DBHelper(val context: Context) : ManagedSQLiteOpenHelper(context, DB_NAME, null, DATABASE_VERSION) {
    companion object {
        private var instance: DBHelper? = null

        @Synchronized
        fun getInstance(context: Context): DBHelper {
            if (instance == null) {
                instance = DBHelper(context.applicationContext)
            }
            return instance!!
        }

        const val ASSETS_PATH = "databases"
        const val DB_NAME = "pokemon.sqlite"
        const val TABLE_POKEMON = "pokemon"
        const val TABLE_POKEMON_DESCRIPTION = "pokemon_description"
        const val TABLE_MASTER_TRAINERS = "master_trainers"
        const val TABLE_MASTER_TRAINER_POKEMON_MOVES = "master_trainer_pokemon_moves"
        const val TABLE_POKEMON_STATS = "pokemon_stats"

        const val DATABASE_VERSION = 1

    }

    private val preferences: SharedPreferences = context.getSharedPreferences(
        "${context.packageName}.database_versions",
        Context.MODE_PRIVATE
    )


    private fun installedDatabaseIsOutdated(): Boolean {
        return preferences.getInt(DB_NAME, 0) < DATABASE_VERSION
    }

    private fun writeDatabaseVersionInPreferences() {
        preferences.edit().apply {
            putInt(DB_NAME, DATABASE_VERSION)
            apply()
        }
    }

    private fun installDatabaseFromAssets() {
        val inputStream = context.assets.open("$ASSETS_PATH/$DB_NAME")
        Log.e("db", "Calling INSTALL ASSETS")

        try {
            val outputFile = File(context.getDatabasePath(DB_NAME).path)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)
            inputStream.close()

            outputStream.flush()
            outputStream.close()
        } catch (exception: Throwable) {
            throw RuntimeException("The $DB_NAME database couldn't be installed.", exception)
        }
    }

    @Synchronized
    private fun installOrUpdateIfNecessary() {
        if (installedDatabaseIsOutdated()) {
            context.deleteDatabase(DB_NAME)
            installDatabaseFromAssets()
            writeDatabaseVersionInPreferences()
        }
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        return super.getWritableDatabase()
        //throw RuntimeException("The $NOTES_DB_NAME database is not writable.")
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        return super.getReadableDatabase()
    }


    fun getPokemonDescription(id: Long): String {
        var value: String = ""
        context.db.use {
            value = select(TABLE_POKEMON_DESCRIPTION, "description")
                .whereSimple("species_id = \"${id}\"")
                .exec { parseList(classParser<String>()) }.get(0)
        }
        return value.replace("\n", " ")
    }


    fun getPokemonId(pokemon_name: String): Long {
        var id: Long = 0

        context.db.use {
            select(TABLE_POKEMON, "id").whereSimple("name=\"${pokemon_name}\"").exec {
                parseList(LongParser).forEach {
                    id = it
                }
            }
        }
        return id
    }

    fun getPokemonStats(id: Long): Pokemon_Stats? {

        var stat: Pokemon_Stats? = null
        context.db.use {
            stat = select(Pokemon_Stats.TABLE_NAME)
                .whereSimple("${Pokemon_Stats.pokemon_id_column} = \"${id}\" and is_mega = 0")
                .exec { parseList(classParser<Pokemon_Stats>()) }.get(0)
        }
        return stat
    }

    fun getMasterTrainer(id: Long): Pokemon_Master_Trainer {

        lateinit var masterTrainer: Pokemon_Master_Trainer
        context.db.use {
            masterTrainer = select(Pokemon_Master_Trainer.TABLE_NAME)
                .whereSimple("${Pokemon_Master_Trainer.pokemon_id_column} = \"${id}\"")
                .exec { parseList(classParser<Pokemon_Master_Trainer>()) }.get(0)
        }
        return masterTrainer
    }

    fun getMasterTrainerPokemonMoves(id: Long) : ArrayList<Pokemon_Master_Trainer_Moves> = context.db.use {
        val moves = ArrayList<Pokemon_Master_Trainer_Moves>()

        select(Pokemon_Master_Trainer_Moves.TABLE_NAME)
            .whereSimple("${Pokemon_Master_Trainer_Moves.pokemon_id_column} = \"${id}\"")
            .parseList(object: MapRowParser<List<Pokemon_Master_Trainer_Moves>> {
                override fun parseRow(columns: Map<String, Any?>): List<Pokemon_Master_Trainer_Moves> {
                    val id = columns.getValue("id")
                    val pokemon_id = columns.getValue("pokemon_id")
                    val move = columns.getValue("move")

                    val pokeMove = Pokemon_Master_Trainer_Moves(id.toString().toLong(), pokemon_id.toString().toLong(), move.toString())
                    moves.add(pokeMove)

                    return moves
                }
            })
        moves
    }

    fun getPokemonAbilities(id:Long): ArrayList<PokemonAbilities> {
        val abilities = ArrayList<PokemonAbilities>()
        val query = "SELECT pokemon_abilities.pokemon_id, abilities.identifier, " +
                "pokemon_abilities.slot, pokemon_abilities.is_hidden "+
                " FROM pokemon_abilities " +
                " INNER JOIN abilities ON " +
                " abilities.id=pokemon_abilities.ability_id " +
                " WHERE pokemon_abilities.pokemon_id = " + id.toString()
        context.db.use {
            val cursor = context.db.readableDatabase.rawQuery(query, null)
            while (cursor.moveToNext()) {
                val ability = PokemonAbilities(id = cursor.getLong(0),ability = cursor.getString(1),
                    slot = cursor.getInt(2), isHidden = cursor.getInt(3))
                abilities.add(ability)
            }
        }
        return abilities
    }




    fun getAllPokemon() : ArrayList<Pokemon> = context.db.use {
        val pokes = ArrayList<Pokemon>()

        select(TABLE_POKEMON, "id", "name", "type1", "type2")
            .parseList(object: MapRowParser<List<Pokemon>> {
                override fun parseRow(columns: Map<String, Any?>): List<Pokemon> {
                    val id = columns.getValue("id")
                    val name = columns.getValue("name")
                    val type1 = columns.getValue("type1")
                    val type2 = columns.getValue("type2")

                    val poke = Pokemon(id.toString().toLong(), name.toString(), type1.toString(), type2.toString())

                    pokes.add(poke)

                    return pokes
                }
            })
        pokes
    }


    override fun onCreate(database: SQLiteDatabase) {
        Log.e("db", "Calling create db")

        /*database.createTable(NOTES_TABLE_NAME, true,
                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "title" to TEXT + NOT_NULL,
                "content" to TEXT,
                "creationDate" to TEXT)*/
        //installDatabaseFromAssets()

    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //database.dropTable(NOTES_TABLE_NAME, ifExists = true)
        Log.e("db", "Calling upgrade db")

    }

}