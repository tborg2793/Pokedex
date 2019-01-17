package com.example.thomas_laughingman.pokedex_master.fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.thomas_laughingman.pokedex_master.PokedexActivity

import com.example.thomas_laughingman.pokedex_master.R
import com.example.thomas_laughingman.pokedex_master.database.db
import com.example.thomas_laughingman.pokedex_master.model.Pokemon_Master_Trainer
import com.example.thomas_laughingman.pokedex_master.model.Pokemon_Master_Trainer_Moves
import org.jetbrains.anko.find
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class PokemonMoreInfo : Fragment() {

    var pokemon_id: Long = 0
    lateinit var pokemon_master_trainer: Pokemon_Master_Trainer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var args = getArguments()

        pokemon_id = args!!.getLong("pokemon_id")


        pokemon_master_trainer = inflater.context.db.getMasterTrainer(pokemon_id)
        var pokemon_master_trainer_moves = inflater.context.db.getMasterTrainerPokemonMoves(pokemon_id)

        val view = inflater.inflate(R.layout.fragment_pokemon_more_info, container, false)
        var masterTrainerNameText = view.findViewById(R.id.master_trainer_name_txtView) as TextView
        var masterTrainerPokemonName = view.findViewById(R.id.master_trainer_pokemon_name_txtView) as TextView
        var masterTrainerPokemonLevel = view.findViewById(R.id.master_trainer_pokemon_level_txtView) as TextView
        var master_trainer_location_txtView = view.find(R.id.master_trainer_location_textView) as TextView
        var master_trainer_location_imgView = view.findViewById(R.id.master_trainer_location_imageView) as ImageView

        var move_slot1_txtView = view.findViewById(R.id.move_slot1_txtView) as TextView
        var move_slot2_txtView = view.findViewById(R.id.move_slot2_txtView) as TextView
        var move_slot3_txtView = view.findViewById(R.id.move_slot3_txtView) as TextView
        var move_slot4_txtView = view.findViewById(R.id.move_slot4_txtView) as TextView


        masterTrainerNameText.setText("Master Trainer: "+pokemon_master_trainer.master_trainer_name)
        masterTrainerPokemonName.setText("Species: "+pokemon_master_trainer.pokemon_name)
        masterTrainerPokemonLevel.setText("Level: "+pokemon_master_trainer.level)
        master_trainer_location_txtView.setText("Location: "+pokemon_master_trainer.location)
        master_trainer_location_imgView.setImageResource(resources.getIdentifier("mastertrainerloc_"+pokemon_id.toString(),"drawable", "com.example.thomas_laughingman.pokedex_master"))


        if (pokemon_master_trainer_moves.count() == 1)
        {
            move_slot1_txtView.setText(pokemon_master_trainer_moves.get(0).move.toString())
            move_slot2_txtView.setText("-")
            move_slot3_txtView.setText("-")
            move_slot4_txtView.setText("-")
        }
        if (pokemon_master_trainer_moves.count() == 2)
        {
            move_slot1_txtView.setText(pokemon_master_trainer_moves.get(0).move.toString())
            move_slot2_txtView.setText(pokemon_master_trainer_moves.get(1).move.toString())
            move_slot3_txtView.setText("-")
            move_slot4_txtView.setText("-")
        }
        if (pokemon_master_trainer_moves.count() == 3)
        {
            move_slot1_txtView.setText(pokemon_master_trainer_moves.get(0).move.toString())
            move_slot2_txtView.setText(pokemon_master_trainer_moves.get(1).move.toString())
            move_slot3_txtView.setText(pokemon_master_trainer_moves.get(2).move.toString())
            move_slot4_txtView.setText("-")
        }
        if (pokemon_master_trainer_moves.count() == 4)
        {
            move_slot1_txtView.setText(pokemon_master_trainer_moves.get(0).move.toString())
            move_slot2_txtView.setText(pokemon_master_trainer_moves.get(1).move.toString())
            move_slot3_txtView.setText(pokemon_master_trainer_moves.get(2).move.toString())
            move_slot4_txtView.setText(pokemon_master_trainer_moves.get(3).move.toString())
        }



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
    }

}
