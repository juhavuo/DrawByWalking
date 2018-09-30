package fi.metropolia.juhavuo.drawbywalking

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.activity_draw.*
import kotlinx.android.synthetic.main.activity_draw.view.*
import kotlinx.android.synthetic.main.save_dialog.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.sqrt

class DrawActivity : AppCompatActivity(), SensorEventListener {

    //code source for sensors: https://developer.android.com/guide/topics/sensors/sensors_position

    private lateinit var sensorManager: SensorManager
    private var rotationSensor: Sensor? = null
    private var accelerationSensor: Sensor? = null
    private var bitmap: Bitmap? = null
    private var fileName: String? = null
    private var direction: Float = 0F
    private var acceleration: Float = 0F
    private val DIRECTORY = Environment.DIRECTORY_PICTURES
    private lateinit var viewHandler: Handler
    private lateinit var updateView:Runnable



    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {

        if(event.sensor == rotationSensor){
            direction = (event.values[2]+1)*180
            testingTextView.text = "$direction"
            //Log.d("view_test","Rotation $direction min $minVal max $maxVal")
        }
        if(event.sensor == accelerationSensor){
            acceleration = sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2])
        }


    }

    fun isExternalStorageUsable(): Boolean{
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        Log.d("view_test","rotation sensor $rotationSensor")


        viewHandler = Handler()
        updateView = Runnable{
            run{

                //draw_view.getParameters(direction,acceleration)
                draw_view.changeCoords()
                draw_view.invalidate()
                viewHandler.postDelayed(updateView,100)
            }
        }

        save_button.setOnClickListener {
            bitmap = draw_view.saveToBitmap()
            Log.d("view_test","${bitmap!!.width} ${bitmap!!.height}")

            val ableToUse = isExternalStorageUsable()
            Log.d("view_test","External storage available: $ableToUse")
            if(ableToUse){
                if(fileName == null){
                    val dialogView = LayoutInflater.from(this).inflate(R.layout.save_dialog,null)
                    val builder = AlertDialog.Builder(this)
                            .setView(dialogView)
                    val alertDialog = builder.show()
                    dialogView.dialog_save_button.setOnClickListener {
                        Log.d("view_test","save_button_clicked")
                        fileName=dialogView.file_name_edittext.text.toString()
                        if(!fileName.isNullOrBlank()) {
                            fileName+=".jpg"
                            saveImageToExternalStorage(fileName!!)
                        }
                        alertDialog.dismiss()
                    }
                    dialogView.dialog_cancel_button.setOnClickListener {
                        Log.d("view_test","cancel_button_clicked")
                        alertDialog.dismiss()
                    }

                }else{
                    saveImageToExternalStorage(fileName!!)
                }
            }
        }



    }

    fun saveImageToExternalStorage(fname: String){
        Log.d("view_test",fname)
        val storageDir = getExternalFilesDir(DIRECTORY).toString()
        val file = File(storageDir,fname)
        Log.d("view_test","${file}")
        try{
            val outputStream = FileOutputStream(file)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG,80,outputStream)
            outputStream.flush()
            outputStream.close()
        }catch (e: IOException){
            Log.e("view_test",e.toString())
        }

    }

    override fun onResume(){
        super.onResume()

        rotationSensor?.also {
            sensorManager.registerListener(this,it,SensorManager.SENSOR_DELAY_UI)
        }
        accelerationSensor?.also {
            sensorManager.registerListener(this,it,SensorManager.SENSOR_DELAY_UI)
        }

        viewHandler.post(updateView)


    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)

        viewHandler.removeCallbacks(updateView)
    }



}
