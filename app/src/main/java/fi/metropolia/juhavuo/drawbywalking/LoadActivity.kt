package fi.metropolia.juhavuo.drawbywalking

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_load.*
import java.io.File
import java.lang.Exception

class LoadActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)

        val storageDirectory: File = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val loadList: MutableList<File> = storageDirectory.listFiles().toMutableList()
        Log.d("view_test","${loadList.size}")
        load_listview.adapter = AdapterForLoadList(this,loadList)


        loadactivity_cancel_button.setOnClickListener {
            finish()
        }
    }
}

private class AdapterForLoadList(context: Context, list: MutableList<File>): BaseAdapter(){

    private val lList: MutableList<File> = list
    private val lContext = context

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
       val layoutInflater = LayoutInflater.from(lContext)
        val loadlist_row = layoutInflater.inflate(R.layout.load_file_row, viewGroup, false)

        val file_name_tw = loadlist_row.findViewById<TextView>(R.id.loadlist_name_textview)
        file_name_tw.text = lList[position].name

        val file_size_tv = loadlist_row.findViewById<TextView>(R.id.loadlist_filesize_textview)
        file_size_tv.text = "${lList[position].totalSpace}"

        val image_view = loadlist_row.findViewById<ImageView>(R.id.imageView)

        try{
            val image_path = lList[position].path
            val bitmap: Bitmap = BitmapFactory.decodeFile(image_path)
            image_view.setImageBitmap(bitmap)
        }catch (e: Exception){
            Log.e("view_test",e.toString())
        }

        image_view.setOnClickListener {
            val intent: Intent = Intent(lContext,DrawActivity::class.java)
            intent.putExtra("file_name",lList[position].name)
            lContext.startActivity(intent)

        }

        val deleteButton = loadlist_row.findViewById<ImageButton>(R.id.loadlist_delete_button)
        deleteButton.setOnClickListener {
            Log.d("view_test","button in position $position pressed")
            lList[position].delete()
            lList.removeAt(position)
            notifyDataSetChanged()
        }

        return loadlist_row
    }

    override fun getItem(position: Int): Any {
        return lList[position]
    }

    override fun getItemId(position: Int): Long {
       return position.toLong()
    }

    override fun getCount(): Int {
        return lList.size
    }



}