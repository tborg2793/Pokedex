package com.example.thomas_laughingman.pokedex_master.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.thomas_laughingman.pokedex_master.*
import com.example.thomas_laughingman.pokedex_master.database.db
import com.example.thomas_laughingman.pokedex_master.model.PokemonGuess
import kotlinx.android.synthetic.main.item_row.view.*


class PokemonGuessAdapter(private var pokeList: List<PokemonGuess>, private val handleFileUpload: HandleFileUpload) : RecyclerView.Adapter<PokemonGuessAdapter.PokeHolder>()  {


    private lateinit var context: Context

    class PokeHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokeHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return PokeHolder(view)
    }

    override fun getItemCount() = pokeList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PokeHolder, position: Int) {

        val currentItem = pokeList[position]

        when {
            currentItem.accuracy > .70 -> holder.itemView.itemAccuracy.setTextColor(ContextCompat.getColor(context, R.color.green))
            currentItem.accuracy < .30 -> holder.itemView.itemAccuracy.setTextColor(ContextCompat.getColor(context, R.color.red))
            else -> holder.itemView.itemAccuracy.setTextColor(ContextCompat.getColor(context, R.color.orange))
        }

        with(holder.itemView) {
            itemName.text = currentItem.name
            itemAccuracy.text = "Probability : ${(currentItem.accuracy * 100).toInt()}%"
            btnYes.setOnClickListener {
                if (isUserSignedIn())
                    handleFileUpload.uploadImageToStorage(currentItem.name, true)
                else
                    startAuth(handleFileUpload as AppCompatActivity)

                val pokemon_id: Long = context.db.getPokemonId(currentItem.name)

                val i = Intent(context.applicationContext, PokemonDetails::class.java)
                i.putExtra("pokemon_id", pokemon_id)
                i.putExtra("pokemon_name", currentItem.name)
                context.startActivity(i)

            }
            btnNo.setOnClickListener {
                showPokemonSpinner()
            }
        }
    }

    fun setList(list: List<PokemonGuess>) {
        pokeList = list
        notifyDataSetChanged()
    }

    private fun showPokemonSpinner() {
        val pokeSpinnerAdapter = ArrayAdapter(context,
            android.R.layout.simple_spinner_item, pokeArray)
        pokeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val view = LayoutInflater.from(context).inflate(R.layout.poke_spinner_dialog, null, false);
        val spinner = view.findViewById<Spinner>(R.id.spinner)
        spinner.adapter = pokeSpinnerAdapter

        val dialog = AlertDialog.Builder(context)
            .setTitle("Help us in making the app better")
            .setMessage("Select correct pokemon from the list below")
            .setView(view)
            .setPositiveButton("Submit") { dialog, _ ->
                handleFileUpload.uploadImageToStorage(spinner.selectedItem as String, false)
                dialog.cancel()
            }
            .create()
        dialog.show()
    }

}