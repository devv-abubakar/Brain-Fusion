@file:Suppress("DEPRECATION")

package devv.abubakar.brainfusion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class Splash : AppCompatActivity() {
    private val splashDelay: Long = 3500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({

            val mainIntent = Intent(this@Splash, MainActivity::class.java)
            startActivity(mainIntent)
            finish()

        }, splashDelay)
    }
}