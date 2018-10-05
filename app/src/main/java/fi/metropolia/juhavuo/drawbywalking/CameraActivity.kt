package fi.metropolia.juhavuo.drawbywalking

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File

class CameraActivity : AppCompatActivity() {

    var filename: String = "temp_photo" //default filename, if user don't write one
    val extension: String = ".jpg"
    var mCurrentPhotoPath: String = ""
    val REQUEST_IMAGE_CAPTURE = 1
    var imageURI: Uri? = null
    var imageFile: File? = null
    var ready_for_photo = false
    var ready_to_proceed = false
    var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        camera_activity_hide_keyboard_button.setOnClickListener {
            if(!camera_activity_filename_edittext.editableText.isNullOrBlank()&&
                    camera_activity_filename_edittext.editableText.length>3){
                filename = camera_activity_filename_edittext.text.toString()
                camera_activity_filename_edittext.editableText.clear()

                ready_for_photo = true
                ready_to_proceed = false
            }
            val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken,InputMethodManager.SHOW_FORCED)
        }

        take_photo_button.setOnClickListener {
            if(ready_for_photo){
                Log.d("photo_test","Ready for picture_taking")
                if(imageFile!=null){
                    imageFile!!.delete()
                }
                imageFile=File.createTempFile(filename,extension,imagePath)
                imageURI = FileProvider.getUriForFile(this,"fi.metropolia.juhavuo.drawbywalking",imageFile!!)
                mCurrentPhotoPath = imageFile!!.absolutePath
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                if(intent.resolveActivity(packageManager)!=null){
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageURI)
                    startActivityForResult(intent,REQUEST_IMAGE_CAPTURE)
                }
            }else{
                Log.d("photo_test","Filename must be at least 4 characters long")
                Toast.makeText(this,"Filename must be at least 4 characters long",Toast.LENGTH_LONG).show()
            }
        }

        camera_activity_cancel_button.setOnClickListener {
            if(imageFile!=null){
                imageFile!!.delete()
            }
            finish()
        }

        camera_activity_proceed_button.setOnClickListener {
            if(ready_to_proceed && imageFile!=null){
                val intent: Intent = Intent(this,DrawActivity::class.java)
                intent.putExtra("file_name",imageFile!!.name)
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Log.d("photo_test","jippii")
            bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
            camera_result_view.setImageBitmap(bitmap)
            ready_to_proceed = true
        }else{
            Log.d("photo_test","${imageFile?.name}")
            imageFile?.delete()
            ready_to_proceed=false
        }
    }
}
