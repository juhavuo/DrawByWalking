package fi.metropolia.juhavuo.drawbywalking

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var viewing_main = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        Log.d("action_bar","$actionBar")
        actionBar?.hide()

        val mainFragment = MainFragment() //at first main fragment is visible
        fragmentManager.beginTransaction().add(R.id.fragment_holder,mainFragment).commit()

        /*
            One can switch between fragments using one button, it changes its label, when pressing it.
         */
        instrctions_fragment_button.setOnClickListener {
            if(viewing_main) {
                val instructionsFragment = InstructionsFragment()
                fragmentManager.beginTransaction().replace(R.id.fragment_holder, instructionsFragment)
                        .addToBackStack(null).commit()
                instrctions_fragment_button.text = getString(R.string.main_activity_fragment_main)
                viewing_main = false
            }else{
                fragmentManager.beginTransaction().replace(R.id.fragment_holder,mainFragment)
                        .addToBackStack(null).commit()
                instrctions_fragment_button.text = getString(R.string.main_activity_fragment_instructions)
                viewing_main = true
            }
        }

        new_button.setOnClickListener {
            val intent: Intent = Intent(this,DrawActivity::class.java)
            startActivity(intent)
        }

        load_from_file_button.setOnClickListener {
            val intent = Intent(this,LoadActivity::class.java)
            startActivity(intent)
        }

        to_camera_activity_button.setOnClickListener {
            val intent = Intent(this,CameraActivity::class.java)
            startActivity(intent)
        }
    }
}
