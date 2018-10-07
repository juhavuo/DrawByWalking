package fi.metropolia.juhavuo.drawbywalking

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import java.io.InputStream
import java.lang.Exception

class InstructionsFragment: Fragment(){

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {


        val view = inflater!!.inflate(R.layout.fragment_instructions,container,false)
        val editText = view.findViewById<TextView>(R.id.instructions_et)
        editText.text = getTheText()
        //editText.keyListener=null
        return view
    }

    fun getTheText():String{
        var text = ""
        try{
            val res = context!!.resources
            val inputStream: InputStream = res.openRawResource(R.raw.instructions)
            text = inputStream.bufferedReader().use {
                it.readText()
            }

        }catch(e: Exception){
            text = e.toString()
        }

        return text

    }

}