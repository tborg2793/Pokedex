package com.example.thomas_laughingman.pokedex_master.fragments


import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView



import android.widget.TextView
import com.example.thomas_laughingman.pokedex_master.database.db

import com.example.thomas_laughingman.pokedex_master.R
import com.example.thomas_laughingman.pokedex_master.model.PokemonAbilities
import com.example.thomas_laughingman.pokedex_master.model.Pokemon_Stats
import com.example.thomas_laughingman.pokedex_master.views.XAxisValueFormatter
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class PokemonBasicInfo : Fragment() {

    private var tts: TextToSpeech? = null
    lateinit var mChart : HorizontalBarChart
    var pokemon_stats: Pokemon_Stats? = null
    var pokemon_id: Long? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var args = getArguments()

        pokemon_id = args?.getLong("pokemon_id")
        val pokemon_name = args?.getString("pokemon_name")

        val pokemon_description: String = inflater.context.db.getPokemonDescription(pokemon_id!!)
        val pokemon_abilities : ArrayList<PokemonAbilities> = inflater.context.db.getPokemonAbilities(pokemon_id!!)

        Log.e("Ability", pokemon_abilities.count().toString())

        pokemon_stats = inflater.context.db.getPokemonStats(pokemon_id!!)

        val view = inflater.inflate(R.layout.fragment_pokemon_basic_info, container, false)
        var nameTxtView = view.findViewById(R.id.pokemon_details_name) as TextView
        var pokemonImageView = view.findViewById(R.id.pokemon_details_image) as ImageView
        var speciesDetailsTxtView = view.findViewById(R.id.pokemon_description_txtView) as TextView
        var pokemon_weight = view.findViewById(R.id.pokemon_weight_txtView) as TextView
        var pokemon_height = view.findViewById(R.id.pokemon_height_txtView) as TextView

        var pokemon_ability_slot1 = view.findViewById(R.id.pokemon_ability_slot1_txtView) as TextView
        var pokemon_ability_slot2 = view.findViewById(R.id.pokemon_ability_slot2_txtView) as TextView
        var pokemon_ability_hidden_slot = view.findViewById(R.id.pokemon_ability_hidden_txtView) as TextView


        nameTxtView.setText(pokemon_name)
        pokemonImageView.setImageResource(resources.getIdentifier("description_"+pokemon_id.toString(),"drawable", "com.example.thomas_laughingman.pokedex_master"))
        speciesDetailsTxtView.setText(pokemon_description)
        pokemon_weight.setText(pokemon_stats?.weight)
        pokemon_height.setText(pokemon_stats?.height)

        Log.e("Size Array", pokemon_abilities.count().toString())

        pokemon_abilities.forEach {
            if(it.slot == 1)
                pokemon_ability_slot1.setText(it.ability)
            else if(it.slot == 2)
                pokemon_ability_slot2.setText(it.ability)
            else if (it.slot == 3)
                pokemon_ability_hidden_slot.setText("Hidden: "+it.ability)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        val labels = ArrayList<String>();

        labels.add("Speed");
        labels.add("SP-Defense");
        labels.add("Defense");
        labels.add("SP-Attack");
        labels.add("Attack");
        labels.add("HP");

        mChart = view.findViewById(R.id.skill_rating_chart) as HorizontalBarChart
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.getDescription().setEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);


        val xl = mChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        val xaxisFormatter = XAxisValueFormatter(labels);
        xl.setValueFormatter(xaxisFormatter);
        xl.setTextSize(15.0f)
        xl.setGranularity(1.0f);

        val yl = mChart.getAxisLeft();
        yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yl.setDrawGridLines(false);
        yl.setEnabled(false);
        yl.setAxisMinimum(0f);

        val yr = mChart.getAxisRight();
        yr.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);

        val yVals1 = ArrayList<BarEntry>();
        yVals1.add(BarEntry(0f, pokemon_stats!!.speed.toFloat())) // Speed
        yVals1.add(BarEntry(1f,pokemon_stats!!.sp_def.toFloat())) // SP-Defense
        yVals1.add(BarEntry(2f,pokemon_stats!!.def.toFloat())) // Defense
        yVals1.add(BarEntry(3f,pokemon_stats!!.sp_atk.toFloat())) // SP-Attack
        yVals1.add(BarEntry(4f,pokemon_stats!!.atk.toFloat())) // Attack
        yVals1.add(BarEntry(5f, pokemon_stats!!.hp.toFloat())) // HP

        var total_stats = view.findViewById(R.id.total_base_stats_txtView) as TextView
        val total_stats_holder = pokemon_stats!!.hp +
                                    pokemon_stats!!.atk +
                                    pokemon_stats!!.sp_atk +
                                    pokemon_stats!!.def +
                                    pokemon_stats!!.sp_def +
                                    pokemon_stats!!.speed
        total_stats.setText("Total " + total_stats_holder.toString())



        val set1: BarDataSet;
        set1 =  BarDataSet(yVals1, "DataSet 1");
        val dataSets = ArrayList<IBarDataSet>();
        dataSets.add(set1);
        val data =  BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(.9f);
        mChart.setData(data);
        mChart.getLegend().setEnabled(false);

    }



}
