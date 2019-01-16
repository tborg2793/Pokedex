package com.example.thomas_laughingman.pokedex_master

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.thomas_laughingman.pokedex_master.database.db
import com.example.thomas_laughingman.pokedex_master.fragments.PokemonBasicInfo
import com.example.thomas_laughingman.pokedex_master.fragments.PokemonMoreInfo
import com.example.thomas_laughingman.pokedex_master.fragments.PokemonMoveList
import kotlinx.android.synthetic.main.activity_pokemon_details.*
import java.util.*
import android.content.Intent



class PokemonDetails : AppCompatActivity() {

    val manager = supportFragmentManager
    var pokemon_id: Long = 0
    var pokemon_name: String = ""

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                createFragmentBasicInfo()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                createFragmentMoves()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                createFragmentMore()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_details)

        val extras = intent.extras
        pokemon_id = extras.getLong("pokemon_id")
        pokemon_name = extras.getString("pokemon_name")

        createFragmentBasicInfo()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onBackPressed() {
        val intent = Intent(this@PokemonDetails, PokedexActivity::class.java)
        startActivity(intent)
    }

    fun createFragmentBasicInfo()
    {
        val transaction = manager.beginTransaction()
        val fragment = PokemonBasicInfo()

        val args = Bundle()
        args.putLong("pokemon_id", pokemon_id)
        args.putString("pokemon_name", pokemon_name)
        fragment.arguments = args
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

   fun createFragmentMoves()
    {
        val transaction = manager.beginTransaction()
        val fragment = PokemonMoveList()

        val args = Bundle()
        args.putLong("pokemon_id", pokemon_id)
        args.putString("pokemon_name", pokemon_name)
        fragment.arguments = args
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun createFragmentMore()
    {
        val transaction = manager.beginTransaction()
        val fragment = PokemonMoreInfo()

        val args = Bundle()
        args.putLong("pokemon_id", pokemon_id)
        args.putString("pokemon_name", pokemon_name)
        fragment.arguments = args
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}
