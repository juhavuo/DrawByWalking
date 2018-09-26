package fi.metropolia.juhavuo.drawbywalking

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_draw.*

class DrawActivity : AppCompatActivity(), SensorEventListener {

    //code source for sensors: https://developer.android.com/guide/topics/sensors/sensors_position

    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rM = FloatArray(9)
    private val iM = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private var azimuth : Int = 0

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
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
