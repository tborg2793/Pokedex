package com.example.thomas_laughingman.pokedex_master

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Typeface
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import com.example.thomas_laughingman.pokedex_master.adapter.PokemonGuessAdapter
import com.example.thomas_laughingman.pokedex_master.model.PokemonGuess
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ml.custom.*
import com.google.firebase.ml.custom.model.FirebaseCloudModelSource
import com.google.firebase.ml.custom.model.FirebaseLocalModelSource
import com.google.firebase.storage.FirebaseStorage
import com.otaliastudios.cameraview.CameraListener
import kotlinx.android.synthetic.main.pokemon_sheet.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

val pokeArray: Array<String> = arrayOf("Abra", "Aerodactyl", "Alakazam", "Arbok", "Arcanine", "Articuno", "Beedrill", "Bellsprout",
    "Blastoise", "Bulbasaur", "Butterfree", "Caterpie", "Chansey", "Charizard", "Charmander", "Charmeleon", "Clefable", "Clefairy", "Cloyster", "Cubone", "Dewgong",
    "Diglett", "Ditto", "Dodrio", "Doduo", "Dragonair", "Dragonite", "Dratini", "Drowzee", "Dugtrio", "Eevee", "Ekans", "Electabuzz",
    "Electrode", "Exeggcute", "Exeggutor", "Farfetchd", "Fearow", "Flareon", "Gastly", "Gengar", "Geodude", "Gloom",
    "Golbat", "Goldeen", "Golduck", "Golem", "Graveler", "Grimer", "Growlithe", "Gyarados", "Haunter", "Hitmonchan",
    "Hitmonlee", "Horsea", "Hypno", "Ivysaur", "Jigglypuff", "Jolteon", "Jynx", "Kabuto",
    "Kabutops", "Kadabra", "Kakuna", "Kangaskhan", "Kingler", "Koffing", "Krabby", "Lapras", "Lickitung", "Machamp",
    "Machoke", "Machop", "Magikarp", "Magmar", "Magnemite", "Magneton", "Mankey", "Marowak", "Meowth", "Metapod",
    "Mew", "Mewtwo", "Moltres", "Mrmime", "Muk", "Nidoking", "Nidoqueen", "Nidorina", "Nidorino", "Ninetales",
    "Oddish", "Omanyte", "Omastar", "Onix", "Paras", "Parasect", "Persian", "Pidgeot", "Pidgeotto", "Pidgey",
    "Pikachu", "Pinsir", "Poliwag", "Poliwhirl", "Poliwrath", "Ponyta", "Porygon", "Primeape", "Psyduck", "Raichu",
    "Rapidash", "Raticate", "Rattata", "Rhydon", "Rhyhorn", "Sandshrew", "Sandslash", "Scyther", "Seadra",
    "Seaking", "Seel", "Shellder", "Slowbro", "Slowpoke", "Snorlax", "Spearow", "Squirtle", "Starmie", "Staryu",
    "Tangela", "Tauros", "Tentacool", "Tentacruel", "Vaporeon", "Venomoth", "Venonat", "Venusaur", "Victreebel",
    "Vileplume", "Voltorb", "Vulpix", "Wartortle", "Weedle", "Weepinbell", "Weezing", "Wigglytuff", "Zapdos", "Zubat")


class PokemonScannerActivity : BaseCameraActivity(), HandleFileUpload {
    companion object {
        /** Dimensions of inputs.  */
        const val DIM_IMG_SIZE_X = 224
        const val DIM_IMG_SIZE_Y = 224
        const val DIM_BATCH_SIZE = 1
        const val DIM_PIXEL_SIZE = 3
        const val IMAGE_MEAN = 128
        private const val IMAGE_STD = 128.0f
    }

    var isRefreshVisible = false

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val rootRef = FirebaseStorage.getInstance().reference.child("pokemon")
    private lateinit var currentBitmap: Bitmap
    private val pokemonList = mutableListOf<PokemonGuess>()
    private val intValues = IntArray(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y)
    private var imgData: ByteBuffer = ByteBuffer.allocateDirect(
        4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE)
    private lateinit var fireBaseInterpreter: FirebaseModelInterpreter
    private lateinit var inputOutputOptions: FirebaseModelInputOutputOptions
    private lateinit var itemAdapter: PokemonGuessAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myToolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        imgData.order(ByteOrder.nativeOrder())
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_UPLOAD, getString(R.string.feedback_notification), NotificationManager.IMPORTANCE_MIN)
            notificationManager.createNotificationChannel(channel)
        }

        rvLabel.layoutManager = LinearLayoutManager(this)
        itemAdapter = PokemonGuessAdapter(pokemonList, this)
        rvLabel.adapter = itemAdapter
//        Load a cloud model using the FirebaseCloudModelSource Builder class
        val cloudSource = FirebaseCloudModelSource.Builder("pokedex")
            .enableModelUpdates(true)
            .build()

        //Registering the cloud model loaded above with the ModelManager Singleton
        FirebaseModelManager.getInstance().registerCloudModelSource(cloudSource)

        //Load a local model using the FirebaseLocalModelSource Builder class
        val fireBaseLocalModelSource = FirebaseLocalModelSource.Builder("pokedex")
            .setAssetFilePath("pokedex_91.tflite")
            .build()

        //Registering the model loaded above with the ModelManager Singleton
        FirebaseModelManager.getInstance().registerLocalModelSource(fireBaseLocalModelSource)

        val firebaseModelOptions = FirebaseModelOptions.Builder()
            .setLocalModelName("pokedex")
            .setCloudModelName("pokedex")
            .build()

        fireBaseInterpreter = FirebaseModelInterpreter.getInstance(firebaseModelOptions)!!

        //Specify the input and outputs of the model
        inputOutputOptions = FirebaseModelInputOutputOptions.Builder()
            .setInputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 224, 224, 3))
            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 149))
            .build()

        //Show target if not already shown
        if (!defaultSharedPreferences.contains("TARGET_INTRO"))
            showTarget()
    }

    private fun showTarget() {
        val cameraFrame = findViewById(R.id.cameraFrame) as ImageView
        TapTargetView.showFor(this,
            TapTarget.forView(cameraFrame, getString(R.string.capture), getString(R.string.capture_message))
                .outerCircleColor(R.color.colorPrimary)
                .outerCircleAlpha(0.95f)
                .targetCircleColor(R.color.colorAccent)
                .titleTextSize(24)
                .titleTextColor(R.color.white)
                .descriptionTextSize(16)
                .descriptionTextColor(R.color.white)
                .textTypeface(Typeface.DEFAULT)
                .drawShadow(true)
                .cancelable(true)
                .tintTarget(true)
                .transparentTarget(true)
                .targetRadius(180),
            object : TapTargetView.Listener() {
                override fun onTargetCancel(view: TapTargetView?) {
                    super.onTargetCancel(view)
                    defaultSharedPreferences.edit().putBoolean("TARGET_INTRO", true).apply()
                }

                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    defaultSharedPreferences.edit().putBoolean("TARGET_INTRO", true).apply()
                }
            }
        )
    }

    //Handle clicks
    override fun onClick(v: View?) {
        val cameraView = findViewById(R.id.cameraView) as com.otaliastudios.cameraview.CameraView
        //the if statement is to alternate between the refresh and image capture functionality of FAB
        if (v?.id == R.id.cameraFrame) {
            progressBar.visibility = View.VISIBLE
            itemAdapter.setList(emptyList())
            cameraView.capturePicture()
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            cameraView.addCameraListener(object : CameraListener() {
                override fun onPictureTaken(jpeg: ByteArray) {
                    isRefreshVisible = true
                    convertByteArrayToBitmap(jpeg)
                }
            })
        }
    }

    fun convertByteArrayToBitmap(byteArray: ByteArray) {
        //Handle this shit in bg
        doAsync {
            val exifInterface = ExifInterface(ByteArrayInputStream(byteArray))
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            //to fix images coming out to be rotated
            //https://github.com/google/cameraview/issues/22#issuecomment-269321811
            val m = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> m.postRotate(90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> m.postRotate(180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> m.postRotate(270F)
            }
            //Create a new bitmap with fixed rotation
            //Crop a part of image that's inside viewfinder and perform detection on that image
            //https://stackoverflow.com/a/8180576/5471095
            //TODO : Need to find a better way to do this than creating a new bitmap
            val cropX = (bitmap.width * 0.2).toInt()
            val cropY = (bitmap.height * 0.25).toInt()

            currentBitmap = Bitmap.createBitmap(bitmap, cropX, cropY, bitmap.width - 2 * cropX, bitmap.height - 2 * cropY, m, true)
            //free up the original bitmap
            bitmap.recycle()

            //create a scaled bitmap for Tensorflow
            val scaledBitmap = Bitmap.createScaledBitmap(currentBitmap, 224, 224, false)
            uiThread {
                getPokemonFromBitmap(scaledBitmap)
            }
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap?): ByteBuffer {
        //Clear the ByteBuffer for a new image
        imgData.rewind()
        bitmap?.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        // Convert the image to floating point.
        var pixel = 0
        for (i in 0 until DIM_IMG_SIZE_X) {
            for (j in 0 until DIM_IMG_SIZE_Y) {
                val currPixel = intValues[pixel++]
                imgData.putFloat(((currPixel shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                imgData.putFloat(((currPixel shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                imgData.putFloat(((currPixel and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
            }
        }
        return imgData
    }

    private fun getPokemonFromBitmap(bitmap: Bitmap) {
        val inputs = FirebaseModelInputs.Builder()
            .add(convertBitmapToByteBuffer(bitmap)) // add() as many input arrays as your model requires
            .build()
        fireBaseInterpreter.run(inputs, inputOutputOptions)
            ?.addOnSuccessListener {
                val pokeList = mutableListOf<PokemonGuess>()
                /**
                 * Run a foreach loop through the output float array containing the probabilities
                 * corresponding to each label
                 * @see pokeArray to know what labels are supported
                 */
                it.getOutput<Array<FloatArray>>(0)[0].forEachIndexed { index, fl ->
                    //Only consider a pokemon when the accuracy is more than 20%
                    if (fl > .20)
                        pokeList.add(PokemonGuess(pokeArray[index], fl))
                }
                itemAdapter.setList(pokeList)
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            ?.addOnFailureListener {
                it.printStackTrace()
                toast("Sorry, there was an error")
            }
            ?.addOnCompleteListener {
                progressBar.visibility = View.GONE
                bitmap.recycle()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pokemon_scanner, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        return true
    }

    //Upload the captured bitmap to Firebase Storage
    override fun uploadImageToStorage(name: String, correct: Boolean) {
        //Collapse the sheet after yes/no was tapped
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val baos = ByteArrayOutputStream()
        currentBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos)
        val data = baos.toByteArray()
        if (isNetworkAvailable()) {
            toast(getString(R.string.thanks_for_feedback))

            if(correct)
            {
                rootRef.child("${FirebaseAuth.getInstance().uid}")
                    .child("detected")
                    .child(name) // ${}
                    .child("${FirebaseAuth.getInstance().currentUser?.displayName?.split(" ")?.first()}${name.toLowerCase()}${System.currentTimeMillis()}.jpg")
                    .putBytes(data)
                    .addOnFailureListener {
                        toast(getString(R.string.feedback_failed))
                    }
                    .addOnCompleteListener {
                        notificationManager.cancel(420)
                        currentBitmap.recycle()
                    }
            }
            else
            {
                rootRef.child("${FirebaseAuth.getInstance().uid}")
                    .child("not detected")
                    .child(name) // ${}
                    .child("${FirebaseAuth.getInstance().currentUser?.displayName?.split(" ")?.first()}${name.toLowerCase()}${System.currentTimeMillis()}.jpg")
                    .putBytes(data)
                    .addOnFailureListener {
                        toast(getString(R.string.feedback_failed))
                    }
                    .addOnCompleteListener {
                        notificationManager.cancel(420)
                        currentBitmap.recycle()
                    }
            }




            showProgressNotification()
        } else {
            toast(getString(R.string.no_network))
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    //Display a notification when the image is uploaded to firebase storage
    private fun showProgressNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_UPLOAD)
            .setContentTitle(getString(R.string.sending_feedback))
            .setContentText(getString(R.string.feedback_in_progress))
            .setSmallIcon(R.drawable.ic_cloud_upload)
            .setProgress(100, 0, true)
            .build()

        notificationManager.notify(420, notification)
    }
}
