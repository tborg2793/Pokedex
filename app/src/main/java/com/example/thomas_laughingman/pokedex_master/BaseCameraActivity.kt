package com.example.thomas_laughingman.pokedex_master

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.otaliastudios.cameraview.Gesture
import com.otaliastudios.cameraview.GestureAction
import kotlinx.android.synthetic.main.activity_pokemon_scanner.*



abstract class BaseCameraActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var sheetBehavior: BottomSheetBehavior<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_scanner)
        sheetBehavior = BottomSheetBehavior.from(bottomLayout)
        sheetBehavior.isHideable = true
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        cameraFrame.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        cameraView.start()
    }

    override fun onPause() {
        cameraView.stop()
        super.onPause()
    }
}