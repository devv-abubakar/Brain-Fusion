package devv.abubakar.brainfusion

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.button.MaterialButton
import devv.abubakar.brainfusion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bounceAnimation(binding.brainAnimation, 2000)
        bounceAnimation(binding.textTagLine, 2100)
        bounceAnimation(binding.textAppDescription, 2200)
        bounceAnimation(binding.btnStart, 2300)
    }

    private fun bounceAnimation(view: View, duration: Int) {
        YoYo.with(Techniques.Bounce)
            .duration(duration.toLong())
            .repeat(100)
            .playOn(view)
    }
}