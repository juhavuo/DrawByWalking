package fi.metropolia.juhavuo.drawbywalking

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_draw.*
import kotlinx.android.synthetic.main.color_chooser.view.*
import kotlinx.android.synthetic.main.save_dialog.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.sqrt

class
DrawActivity : AppCompatActivity(), SensorEventListener {

    //code source for sensors: https://developer.android.com/guide/topics/sensors/sensors_position

    private lateinit var sensorManager: SensorManager
    private var rotationSensor: Sensor? = null
    private var accelerationSensor: Sensor? = null
    private var bitmap: Bitmap? = null
    private var fileName: String? = null
    private var direction: Float = 0F
    private var acceleration: Float = 0F
    private val DIRECTORY = Environment.DIRECTORY_PICTURES
    private var isDrawing: Boolean = false
    private lateinit var viewHandler: Handler
    private lateinit var updateView:Runnable
    private var drawing_batch = 0 //to what shape point belongs to
    private var pen_r = 0 //keep track of the pen colors
    private var pen_g = 0// --||--
    private var pen_b = 0// --||--


    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {

        if(event.sensor == rotationSensor){
            direction = 360-(event.values[2]+1)*180
            //Log.d("view_test","Rotation $direction min $minVal max $maxVal")
        }
        if(event.sensor == accelerationSensor){

            acceleration = sqrt(event.values[1]*event.values[1])
            if(acceleration<0.1){
                acceleration=0F
            }
        }
    }

    fun isExternalStorageUsable(): Boolean{
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        //if one gets here from LoadActivity, the filename is taken from extras and bitmap fetched
        val bundle = intent.extras
        if(bundle != null){
            Log.d("view_test","${bundle}")
            val path: String= this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).path+"/"+bundle.getString("file_name")
            fileName = bundle.getString("file_name")
            val file: File = File(path)
            Log.d("view_test","${file.totalSpace}")
            bitmap = BitmapFactory.decodeFile(path)
            draw_view.setBitmap(bitmap!!)
        }else{
            openColorChooserDialog("Choose background color",0)
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        Log.d("view_test","rotation sensor $rotationSensor")

        viewHandler = Handler()
        updateView = Runnable{
            run{

                //draw_view.getParameters(direction,acceleration)
                if(acceleration>0) {
                    draw_view.getParameters(direction, acceleration,drawing_batch)
                    draw_view.invalidate()
                }
                viewHandler.postDelayed(updateView,100)
            }
        }

        //set original color to pen display view
        pen_color_view.setBackgroundColor(Color.rgb(pen_r,pen_g,pen_b))

        draw_view.setOnTouchListener{v: View, m: MotionEvent->

            if(isDrawing){
                draw_activity_layout.setBackgroundColor(ContextCompat.getColor(this,R.color.normal))
                isDrawing = false
                viewHandler.removeCallbacks(updateView)
            }else{
                draw_activity_layout.setBackgroundColor(ContextCompat.getColor(this,R.color.recording))
                drawing_batch++
                draw_view.setLocation(m.x,m.y)
                isDrawing = true
                viewHandler.post(updateView)

            }
            false
        }

        change_drawing_color_button.setOnClickListener {
            openColorChooserDialog("Choose pen color",1 )
        }

        save_button.setOnClickListener {
            bitmap = draw_view.saveToBitmap()
            Log.d("view_test","${bitmap!!.width} ${bitmap!!.height}")

            val ableToUse = isExternalStorageUsable()
            Log.d("view_test","External storage available: $ableToUse")
            if(ableToUse){
                if(fileName == null){ //if there is no given filename user is prompted to give one
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

                }else{ //file will be saved with the name it already has
                    saveImageToExternalStorage(fileName!!)
                }
            }
        }

        go_to_main_activity_button.setOnClickListener {
            popToRoot()
        }

        remove_points_button.setOnClickListener {
            draw_view.removeValuesFromPointList()
        }


        val spinner_values = IntArray(10 ){it*5+5}.toTypedArray() //set values for pen size
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, spinner_values)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        pen_size_spinner.adapter = adapter
        draw_view.changeSize(spinner_values[pen_size_spinner.selectedItemPosition].toFloat())
        pen_size_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("spinner_test","${spinner_values[position]}")
                draw_view.changeSize(spinner_values[position].toFloat())
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

    /*
        Start sensors, when activity restarts
     */
    override fun onResume(){
        super.onResume()

        rotationSensor?.also {
            sensorManager.registerListener(this,it,SensorManager.SENSOR_DELAY_UI)
        }
        accelerationSensor?.also {
            sensorManager.registerListener(this,it,SensorManager.SENSOR_DELAY_UI)
        }
    }

    /*
        When activity pauses, sensors are deactivated
     */
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)

        viewHandler.removeCallbacks(updateView)
    }

    override fun onBackPressed() {
        popToRoot()
    }

    //https://stackoverflow.com/questions/2776830/android-moving-back-to-first-activity-on-button-click
    //to go to main activity even if came to this activity via load activity
    fun Context.popToRoot(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    /*
        This creates popup dialog, where one can set color with three seekbars and which has color preview
     */
    fun openColorChooserDialog(color_string: String, option: Int){ //option 0 = background color, option 1 = pen color
        var c = 0
        val colorChooserView = LayoutInflater.from(this).inflate(R.layout.color_chooser,null)
        val builder = AlertDialog.Builder(this)
                .setView(colorChooserView)
        val alertDialog = builder.show()
        if(option == 0) {
            colorChooserView.seekBar_r.progress = 100
            colorChooserView.seekBar_b.progress = 100
            colorChooserView.seekBar_g.progress = 100
        }else{
            colorChooserView.seekBar_r.progress = pen_r
            colorChooserView.seekBar_g.progress = pen_g
            colorChooserView.seekBar_b.progress = pen_b
        }
        var r_value = colorChooserView.seekBar_r.progress
        var g_value = colorChooserView.seekBar_g.progress
        var b_value = colorChooserView.seekBar_b.progress
        colorChooserView.color_preview.setBackgroundColor(Color.rgb(r_value,g_value,b_value))
        colorChooserView.color_chooser_title.text = color_string

        colorChooserView.seekBar_r.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                r_value = progress
                colorChooserView.color_preview.setBackgroundColor(Color.rgb(r_value,g_value,b_value))
                colorChooserView.color_preview.invalidate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        colorChooserView.seekBar_g.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                g_value = progress
                colorChooserView.color_preview.setBackgroundColor(Color.rgb(r_value,g_value,b_value))
                colorChooserView.color_preview.invalidate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        colorChooserView.seekBar_b.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                b_value = progress
                colorChooserView.color_preview.setBackgroundColor(Color.rgb(r_value,g_value,b_value))
                colorChooserView.color_preview.invalidate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        colorChooserView.button.setOnClickListener {
            Log.d("seekbar_test","$r_value $g_value $b_value")
            c = Color.rgb(r_value,g_value,b_value)
            if(option == 0) {
                draw_view.setBackgroundColor(c)
            }else if(option == 1){
                draw_view.changeColor(c)
                pen_r = r_value
                pen_g = g_value
                pen_b = b_value
                pen_color_view.setBackgroundColor(Color.rgb(r_value,g_value,b_value))
            }
            alertDialog.dismiss()
        }
    }
}
