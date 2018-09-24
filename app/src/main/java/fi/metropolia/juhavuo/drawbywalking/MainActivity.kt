package fi.metropolia.juhavuo.drawbywalking

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        new_button.setOnClickListener {
            val intent: Intent = Intent(this,DrawActivity::class.java)
            startActivity(intent)
        }
    }
}
