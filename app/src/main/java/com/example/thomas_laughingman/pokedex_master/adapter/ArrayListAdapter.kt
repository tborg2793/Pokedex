package com.example.thomas_laughingman.pokedex_master.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.thomas_laughingman.pokedex_master.R
import com.example.thomas_laughingman.pokedex_master.model.Pokemon
import kotlinx.android.synthetic.main.pokemon_item_list.view.*

class ArrayListAdapter(var context: Context, var pokemon: ArrayList<Pokemon>): BaseAdapter() {

    /*companion object {
        private val LABEL_COLORS = hashMapOf(
            "Low-Carb" to R.color.colorLowCarb,
            "Low-Fat" to R.color.colorLowFat,
            "Low-Sodium" to R.color.colorLowSodium,
            "Medium-Carb" to R.color.colorMediumCarb,
            "Vegetarian" to R.color.colorVegetarian,
            "Balanced" to R.color.colorBalanced
        )
    }*/
    private class ViewHolder(row: View?){
        var txtName: TextView
        var ivImage: ImageView
        var type1: TextView
        var type2: TextView

        init{
            this.txtName = row?.findViewById(R.id.txtName) as TextView
            this.ivImage = row?.findViewById(R.id.ivPokemon) as ImageView
            this.type1 = row?.findViewById(R.id.txtType1) as TextView
            this.type2 = row?.findViewById(R.id.txtType2) as TextView
        }
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View?
        var viewHolder: ViewHolder

        /*detailTextView.setTextColor(
            ContextCompat.getColor(context, LABEL_COLORS[recipe.label] ?: R.color.colorPrimary))*/

        if(convertView == null)
        {
            var layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.pokemon_item_list, parent, false)
            viewHolder = ViewHolder(view)
            view.tag =  viewHolder
        }
        else
        {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        var pokemon: Pokemon = getItem(position) as Pokemon


        viewHolder.txtName.text = pokemon.name
        viewHolder.ivImage.setImageResource(context.resources.getIdentifier("sprite_"+pokemon.name.toLowerCase(), "drawable", context.packageName))
        viewHolder.type1.text = pokemon.type1
        viewHolder.type2.text = pokemon.type2


        return view as View
    }

    override fun getItem(position: Int): Any {
        return pokemon.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return pokemon.count()
    }

}





