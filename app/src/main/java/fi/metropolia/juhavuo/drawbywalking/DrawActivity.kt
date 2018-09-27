package fi.metropolia.juhavuo.drawbywalking

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.activity_draw.*
import kotlinx.android.synthetic.main.activity_draw.view.*
import kotlinx.android.synthetic.main.save_dialog.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DrawActivity : AppCompatActivity(), SensorEventListener {

    //code source for sensors: https://developer.android.com/guide/topics/sensors/sensors_position

    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rM = FloatArray(9)
    private val iM = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private var azimuth : Int = 0
    private var bitmap: Bitmap? = null
    private var fileName: String? = null
    private val DIRECTORY = Environment.DIRECTORY_PICTURES

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }

        SensorManager.getRotationMatrix(
                rM,
                iM,
                accelerometerReading,
                magnetometerReading
        )

        SensorManager.getOrientation(rM,orientationAngles)
        azimuth = Math.toDegrees((orientationAngles[0].toDouble()+360)%360).toInt()
        testingTextView.text = "${azimuth}"


    }

    fun updateOrientationAngles() {

        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
                rM,
                null,
                accelerometerReading,
                magnetometerReading
        )


        /*
        // "mrM" now has up-to-date information.

        SensorManager.getOrientation(rM, orientationAngles)
        //Log.d("sensor_testing","${orientationAngles[0]*360}")
        azimuth = Math.toDegrees(Math.atan(rM[1].toDouble()-rM[3].toDouble()/
                (rM[0].toDouble()+rM[4].toDouble()))).toInt()
        textView2.text = "$azimuth"

        // "mOrientationAngles" now has up-to-date information.*/


    }
    fun isExternalStorageUsable(): Boolean{
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
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

    override fun onResume() {
        super.onResume()
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                    this,
                    magneticField,
                    SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

}
