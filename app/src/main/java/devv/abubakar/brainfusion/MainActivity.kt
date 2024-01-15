package devv.abubakar.brainfusion

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import devv.abubakar.brainfusion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isInternetAvailable(this)) {
            // Launch NoInternetActivity and optionally finish the main activity
            startActivity(Intent(this, NoInternetActivity::class.java))
            finish()
        } else {

            bounceAnimation(binding.brainAnimation, 2000)
            bounceAnimation(binding.textTagLine, 2100)
            bounceAnimation(binding.textAppDescription, 2200)
            bounceAnimation(binding.btnStart, 2300)
        }
    }

    private fun bounceAnimation(view: View, duration: Int) {
        YoYo.with(Techniques.Bounce)
            .duration(duration.toLong())
            .repeat(100)
            .playOn(view)
    }
}