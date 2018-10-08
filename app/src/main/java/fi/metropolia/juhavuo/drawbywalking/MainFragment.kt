package fi.metropolia.juhavuo.drawbywalking


import android.app.Fragment
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

/*
    This fragment is used in main activity to show title image of this application
 */
class MainFragment: Fragment(){

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_main,container,false)
        return view
    }
}