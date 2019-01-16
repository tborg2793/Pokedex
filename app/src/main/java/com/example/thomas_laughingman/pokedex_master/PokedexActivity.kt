package com.example.thomas_laughingman.pokedex_master

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.example.thomas_laughingman.pokedex_master.adapter.ArrayListAdapter
import com.example.thomas_laughingman.pokedex_master.database.DBHelper.Companion.TABLE_POKEMON
import com.example.thomas_laughingman.pokedex_master.model.Pokemon
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.select
import com.example.thomas_laughingman.pokedex_master.database.db
import android.content.Intent




class PokedexActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokedex)

        var pokes = db.getAllPokemon()

        var listView = findViewById(R.id.listView) as? ListView

        listView?.adapter = ArrayListAdapter(applicationContext, pokes)


        listView?.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View,
                                     position: Int, id: Long) {

                // value of item that is clicked
                val itemValue = listView?.getItemAtPosition(position) as Pokemon

                val i = Intent(this@PokedexActivity, PokemonDetails::class.java)
                i.putExtra("pokemon_id", itemValue.id)
                i.putExtra("pokemon_name", itemValue.name)
                startActivity(i)

                // Toast the values
                /*Toast.makeText(applicationContext,
                    "Position :$position\nPokemon ID : ${itemValue.id}\nPokemon Name : ${itemValue.name}", Toast.LENGTH_LONG)
                    .show()*/
            }
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this@PokedexActivity, MainActivity::class.java)
        startActivity(intent)
    }


}
